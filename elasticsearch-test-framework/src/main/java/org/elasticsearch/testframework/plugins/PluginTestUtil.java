package org.elasticsearch.testframework.plugins;

import org.elasticsearch.plugins.MetaPluginInfo;
import org.elasticsearch.plugins.PluginInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Utility methods for testing plugins.
 */
public class PluginTestUtil {
    public static void writeMetaPluginProperties(Path pluginDir, String... stringProps) throws IOException {
        writeProperties(pluginDir.resolve(MetaPluginInfo.ES_META_PLUGIN_PROPERTIES), stringProps);
    }

    public static void writePluginProperties(Path pluginDir, String... stringProps) throws IOException {
        writeProperties(pluginDir.resolve(PluginInfo.ES_PLUGIN_PROPERTIES), stringProps);
    }

    /** convenience method to write a plugin properties file */
    private static void writeProperties(Path propertiesFile, String... stringProps) throws IOException {
        assert stringProps.length % 2 == 0;
        Files.createDirectories(propertiesFile.getParent());
        Properties properties =  new Properties();
        for (int i = 0; i < stringProps.length; i += 2) {
            properties.put(stringProps[i], stringProps[i + 1]);
        }
        try (OutputStream out = Files.newOutputStream(propertiesFile)) {
            properties.store(out, "");
        }
    }
}
