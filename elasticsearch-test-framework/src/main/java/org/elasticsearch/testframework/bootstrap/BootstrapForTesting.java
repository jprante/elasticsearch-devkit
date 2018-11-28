package org.elasticsearch.testframework.bootstrap;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.bootstrap.Bootstrap;
import org.elasticsearch.bootstrap.BootstrapInfo;
import org.elasticsearch.bootstrap.JarHell;
import org.elasticsearch.bootstrap.Security;
import org.elasticsearch.common.Booleans;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.network.IfConfig;
import org.elasticsearch.plugins.PluginInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;

import static com.carrotsearch.randomizedtesting.RandomizedTest.systemPropertyAsBoolean;

/**
 * Initializes natives and installs test security manager
 * (init'd early by base classes to ensure it happens regardless of which
 * test case happens to be first, test ordering, etc).
 * The idea is to mimic as much as possible what happens with ES in production
 * mode (e.g. assign permissions and install security manager the same way).
 */
public class BootstrapForTesting {

    private static final Logger logger = ESLoggerFactory.getLogger(BootstrapForTesting.class);

    static {

        // make sure java.io.tmpdir exists always (in case code uses it in a static initializer)
        Path javaTmpDir = PathUtils.get(Objects.requireNonNull(System.getProperty("java.io.tmpdir"),
                                                               "please set ${java.io.tmpdir} in pom.xml"));
        try {
            Security.ensureDirectoryExists(javaTmpDir);
        } catch (Exception e) {
            throw new RuntimeException("unable to create test temp directory", e);
        }

        // just like bootstrap, initialize natives, then SM
        final boolean systemCallFilter = Booleans.parseBoolean(System.getProperty("tests.system_call_filter", "true"));
        Bootstrap.initializeNatives(javaTmpDir, true, systemCallFilter, true);

        // initialize probes
        Bootstrap.initializeProbes();

        // initialize sysprops
        BootstrapInfo.getSystemProperties();

        // check for jar hell
        try {
            Logger jarHellLogger = ESLoggerFactory.getLogger(JarHell.class);
            JarHell.checkJarHell(jarHellLogger::debug);
        } catch (Exception e) {
            throw new RuntimeException("found jar hell in test classpath", e);
        }

        // Log ifconfig output before SecurityManager is installed
        IfConfig.logIfNecessary();

        // install security manager if requested
        if (systemPropertyAsBoolean("tests.security.manager", true)) {
            try {
                SecurityForTesting.configure(javaTmpDir, true);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("unable to install test security manager", e);
            }
        }

        // guarantee plugin classes are initialized first, in case they have one-time hacks.
        // this just makes unit testing more realistic
        try {
            Enumeration<URL> pluginPropsUrls = BootstrapForTesting.class.getClassLoader().getResources(PluginInfo.ES_PLUGIN_PROPERTIES);
            for (URL pluginPropsUrl : Collections.list(pluginPropsUrls)) {
                Properties properties = new Properties();
                try (InputStream stream = FileSystemUtils.openFileURLStream(pluginPropsUrl)) {
                    properties.load(stream);
                }
                String clazz = properties.getProperty("classname");
                if (clazz != null) {
                    logger.info("loading plugin class: " + clazz);
                    Class.forName(clazz);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("unable to instantiate plugins", e);
        }
    }

    // does nothing, just easy way to make sure the class is loaded.
    public static void ensureInitialized() {}
}
