package org.elasticsearch.testframework.bootstrap;

import com.carrotsearch.randomizedtesting.RandomizedRunner;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.testframework.util.LuceneTestCase;
import org.elasticsearch.bootstrap.Bootstrap;
import org.elasticsearch.bootstrap.ESPolicy;
import org.elasticsearch.bootstrap.FilePermissionUtils;
import org.elasticsearch.bootstrap.Security;
import org.elasticsearch.common.Booleans;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.SuppressForbidden;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.secure_sm.SecureSM;
import org.junit.Assert;

import java.io.IOException;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.Permission;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SecurityForTesting {

    private static final Logger logger = ESLoggerFactory.getLogger(SecurityForTesting.class);

    public static void configure(Path javaTmpDir, boolean filterBadDefaults)
            throws IOException, URISyntaxException {

        Set<URI> modulePath = Security.parseModulePath();
        Map<String, URI> codebase = Security.createCodebase(modulePath);
        Map<String, String> systemProperties = Security.createSystemProperties(codebase);
        Security.setSystemProperties(systemProperties);
        Permissions permissions = createPermissions(modulePath, javaTmpDir);
        Map<URI, Policy> pluginPolicies = getPluginPermissions(systemProperties);
        try {
            ESPolicy esPolicy = Security.createPolicy(permissions, pluginPolicies, filterBadDefaults);
            // read test framework permissions
            URL url = BootstrapForTesting.class.getResource("elasticsearch-test-framework.policy");
            Policy tmpTestFrameworkPolicy;
            if (url != null) {
                logger.info("reading test framework policy: " + url);
                tmpTestFrameworkPolicy = ESPolicy.readPolicy(url.toURI());
            } else {
                throw new RuntimeException("unable to read test framework policy " + url);
            }
            final Policy testFrameworkPolicy = tmpTestFrameworkPolicy;
            // extra policy
            Policy tmpExtraPolicy = null;
            String extraSecurity = System.getProperty("tests.security.policy");
            if (extraSecurity != null) {
                url = BootstrapForTesting.class.getResource(extraSecurity);
                if (url != null) {
                    logger.info("reading extra security policy: " + url);
                    tmpExtraPolicy = ESPolicy.readPolicy(url.toURI());
                }
            }
            final Policy extraPolicy = tmpExtraPolicy;
            Policy.setPolicy(new Policy() {
                @Override
                public boolean implies(ProtectionDomain domain, Permission permission) {
                    // implements union of all policies
                    return esPolicy.implies(domain, permission) ||
                            testFrameworkPolicy.implies(domain, permission) ||
                            (extraPolicy != null && extraPolicy.implies(domain, permission));
                }
            });
        } finally {
            Security.clearSystemProperties(systemProperties);
        }
        final String[] classesThatCanExit = new String[]{
                // surefire test runner
                "org\\.apache\\.maven\\.surefire\\.booter\\..*",
                // junit4 test runner
                "com\\.carrotsearch\\.ant\\.tasks\\.junit4\\.slave\\..*",
                // eclipse test runner
                "org\\.eclipse.jdt\\.internal\\.junit\\.runner\\..*",
                // intellij test runner
                "com\\.intellij\\.rt\\.execution\\.junit\\..*"
        };
        SecureSM secureSM = new SecureSM(classesThatCanExit);
        // enable security manager
        System.setSecurityManager(secureSM);
        Security.selfTest();
    }

    private static Permissions createPermissions(Set<URI> modulePath, Path javaTmpDir) throws IOException {
        Permissions perms = new Permissions();
        Security.addModulePathPermissions(modulePath, perms);
        // java.io.tmpdir
        FilePermissionUtils.addDirectoryPath(perms, "java.io.tmpdir", javaTmpDir, "read,readlink,write,delete");
        // custom test config file
        if (Strings.hasLength(System.getProperty("tests.config"))) {
            FilePermissionUtils.addSingleFilePath(perms, PathUtils.get(System.getProperty("tests.config")), "read,readlink");
        }
        // jacoco coverage output file
        final boolean testsCoverage = Booleans.parseBoolean(System.getProperty("tests.coverage", "false"));
        if (testsCoverage) {
            Path coverageDir = PathUtils.get(System.getProperty("tests.coverage.dir"));
            FilePermissionUtils.addSingleFilePath(perms, coverageDir.resolve("jacoco.exec"), "read,write");
            // in case we get fancy and use the -integration goals later:
            FilePermissionUtils.addSingleFilePath(perms, coverageDir.resolve("jacoco-it.exec"), "read,write");
        }
        // intellij hack: intellij test runner wants setIO and will
        // screw up all test logging without it!
        if (System.getProperty("tests.gradle") == null) {
            perms.add(new RuntimePermission("setIO"));
        }
        // add bind permissions for testing
        // ephemeral ports (note, on java 7 before update 51, this is a different permission)
        // this should really be the only one allowed for tests, otherwise they have race conditions
        perms.add(new SocketPermission("localhost:0", "listen,resolve"));
        // ... but tests are messy. like file permissions, just let them live in a fantasy for now.
        // TODO: cut over all tests to bind to ephemeral ports
        perms.add(new SocketPermission("localhost:1024-", "listen,resolve"));
        return perms;
    }

    /**
     * we don't know which codesources belong to which plugin, so just remove the permission from key codebases
     * like core, test-framework, etc. this way tests fail if accesscontroller blocks are missing.
     */
    @SuppressForbidden(reason = "accesses fully qualified URLs to configure security")
    private static Map<URI, Policy> getPluginPermissions(Map<String, String> systemProperties) throws IOException {
        Map<URI, Policy> map = new LinkedHashMap<>();
        try {
            List<URL> pluginPolicyURLs = Collections.list(BootstrapForTesting.class.getClassLoader()
                    .getResources(PluginInfo.ES_PLUGIN_POLICY));
            if (pluginPolicyURLs.isEmpty()) {
                logger.warn("no plugin policies");
                return Collections.emptyMap();
            }
            // compute module path minus obvious places, all other jars will get the permission.
            Set<URI> codebases = new HashSet<>(parseModulePathWithSymlinks());
            Set<URI> excluded = new HashSet<>(Arrays.asList(
                    // es core
                    Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                    // es test framework
                    BootstrapForTesting.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                    // lucene test framework
                    LuceneTestCase.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                    // randomized runner
                    RandomizedRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                    // junit library
                    Assert.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            ));
            codebases.removeAll(excluded);

            final List<Policy> policies = new ArrayList<>(pluginPolicyURLs.size());
            Map<String, String> pluginSystemProperties = Security.createSystemProperties(Security.createCodebase(codebases));
            Security.setSystemProperties(pluginSystemProperties);
            systemProperties.putAll(pluginSystemProperties);
            // parse each policy file, with codebase substitution from the classpath
            for (URL policyFile : pluginPolicyURLs) {
                policies.add(ESPolicy.readPolicy(policyFile.toURI()));
            }
            // consult each policy file for those codebases
            for (URI uri : codebases) {
                map.put(uri, new Policy() {
                    @Override
                    public boolean implies(ProtectionDomain domain, Permission permission) {
                        // implements union
                        for (Policy p : policies) {
                            if (p.implies(domain, permission)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Return parsed module path, but with symlinks resolved to destination files for matching
     * this is for matching the toRealPath() in the code where we have a proper plugin structure.
     */
    @SuppressForbidden(reason = "does evil stuff with paths and urls because devs and jenkins do evil stuff with paths and urls")
    private static Set<URI> parseModulePathWithSymlinks() throws Exception {
        Set<URI> raw = Security.parseModulePath();
        Set<URI> cooked = new HashSet<>(raw.size());
        Set<URI> uris = new LinkedHashSet<>();
        for (URI uri : raw) {
            if (uri.getScheme().equals("jrt")) {
                // if module path, there is no such thing as symlink or dubious paths, avoid PathUtils
                uris.add(uri);
            } else {
                boolean added = cooked.add(PathUtils.get(uri).toRealPath().toUri());
                if (!added) {
                    throw new IllegalStateException("Duplicate in module path after resolving symlinks: " + uri);
                }
                uris.add(uri);
            }
        }
        return uris;
    }
}
