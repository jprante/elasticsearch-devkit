package org.elasticsearch.testframework.common.io;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 *  Look for tree-structured grouped classpath resources like yml test suites. Works with jars and class directories.
 */
public class ResourceUtilsForTesting {

    private static final Logger logger = Logger.getLogger(ResourceUtilsForTesting.class.getName());

    public static Map<String, Collection<URL>> getResourceGroups(ClassLoader classLoader, String root,
                                                                 List<String> groups)
            throws URISyntaxException, IOException {
        Map<String, Collection<URL>> result = new LinkedHashMap<>();
        return getResourceGroups(classLoader, root, groups, result);
    }

    private static Map<String, Collection<URL>> getResourceGroups(ClassLoader classLoader, String root,
                                                                 List<String> groups,  Map<String, Collection<URL>> result)
            throws URISyntaxException, IOException {
        for (String group : groups) {
            List<String> descriptors = new ArrayList<>();
            if (group.isEmpty()) {
                descriptors.add(root + "/");
            } else {
                if (group.contains("/")) {
                    descriptors.add(root + "/" + group + ".yml");
                } else {
                    descriptors.add(root + "/" + group + "/");
                }
            }
            logger.info("descriptors = " + descriptors);
            for (String descriptor : descriptors) {
                URL url = classLoader.getResource(descriptor);
                if (url != null) {
                    switch (url.getProtocol()) {
                        case "file":
                            File descriptorFile = new File(url.toURI());
                            if (descriptorFile.isFile()) {
                                URL entryURL = new URL(url, descriptorFile.getName());
                                update(result, group, entryURL);
                            } else {
                                File[] files = descriptorFile.listFiles();
                                if (files != null) {
                                    for (File file : files) {
                                        if (file.isFile()) {
                                            URL entryURL = new URL(url, file.getName());
                                            update(result, group, entryURL);
                                        } else if (file.isDirectory()) {
                                            getResourceGroups(classLoader, root + "/" + file.getName(), groups, result);
                                        }
                                    }
                                }
                            }
                            break;
                        case "jar":
                            String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                            JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry jarEntry = entries.nextElement();
                                logger.info("jar entry:  " + jarEntry);
                                if (!jarEntry.isDirectory()) {
                                    String name = jarEntry.getName();
                                    if (name.equals(descriptor)) {
                                        update(result, group, url);
                                    } else if (name.startsWith(descriptor)) {
                                        String entry = name.substring(descriptor.length());
                                        if (entry.startsWith("/")) {
                                            entry = entry.substring(1);
                                        }
                                        URL entryURL = new URL(url, entry);
                                        update(result, group, entryURL);
                                    }
                                }
                            }
                            break;
                        default:
                            throw new RuntimeException("unprocesseed URL scheme in " + url);
                    }
                }
            }
        }
        return result;
    }

    private static void update(Map<String, Collection<URL>> result, String group, URL entryUrl) {
        if (!group.isEmpty()) {
            if (group.contains("/")) {
                group = group.substring(0, group.indexOf("/"));
            }
            Collection<URL> urls = result.getOrDefault(group, new LinkedHashSet<>());
            urls.add(entryUrl);
            result.put(group, urls);
        } else {
            String derivedGroup;
            String s = entryUrl.toString();
            int i = s.lastIndexOf("/");
            if (i >= 0) {
                int j = s.substring(0, i).lastIndexOf("/");
                if (j >= 0) {
                    derivedGroup = s.substring(j + 1, i);
                    Collection<URL> urls = result.getOrDefault(derivedGroup, new LinkedHashSet<>());
                    urls.add(entryUrl);
                    result.put(derivedGroup, urls);
                }
            }
        }
    }
}
