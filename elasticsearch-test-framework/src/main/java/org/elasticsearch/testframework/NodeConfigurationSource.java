package org.elasticsearch.testframework;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public abstract class NodeConfigurationSource {

    public static final NodeConfigurationSource EMPTY = new NodeConfigurationSource() {
        @Override
        public Settings nodeSettings(int nodeOrdinal) {
            return Settings.EMPTY;
        }

        @Override
        public Path nodeConfigPath(int nodeOrdinal) {
            return null;
        }

        @Override
        public Settings transportClientSettings() {
            return Settings.EMPTY;
        }
    };

    /**
     * @return the settings for the node represented by the given ordinal, or {@code null} if there are no settings defined
     */
    public abstract Settings nodeSettings(int nodeOrdinal);

    public abstract Path nodeConfigPath(int nodeOrdinal);

    /** Returns plugins that should be loaded on the node */
    public Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.emptyList();
    }

    public Settings transportClientSettings() {
        return Settings.EMPTY;
    }

    /** Returns plugins that should be loaded in the transport client */
    public Collection<Class<? extends Plugin>> transportClientPlugins() {
        return Collections.emptyList();
    }

}
