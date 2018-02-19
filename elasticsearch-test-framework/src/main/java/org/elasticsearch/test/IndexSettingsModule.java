package org.elasticsearch.test;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexSettings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IndexSettingsModule extends AbstractModule {

    private final Index index;
    private final Settings settings;

    public IndexSettingsModule(Index index, Settings settings) {
        this.settings = settings;
        this.index = index;
    }

    @Override
    protected void configure() {
        bind(IndexSettings.class).toInstance(newIndexSettings(index, settings));
    }

    public static IndexSettings newIndexSettings(String index, Settings settings, Setting<?>... setting) {
        return newIndexSettings(new Index(index, settings.get(IndexMetaData.SETTING_INDEX_UUID,
                IndexMetaData.INDEX_UUID_NA_VALUE)), settings, setting);
    }

    public static IndexSettings newIndexSettings(Index index, Settings settings, Setting<?>... setting) {
        return newIndexSettings(index, settings, Settings.EMPTY, setting);
    }

    public static IndexSettings newIndexSettings(Index index, Settings indexSetting, Settings nodeSettings, Setting<?>... setting) {
        Settings build = Settings.builder().put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
            .put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 1)
            .put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, 1)
            .put(indexSetting)
            .build();
        IndexMetaData metaData = IndexMetaData.builder(index.getName()).settings(build).build();
        Set<Setting<?>> settingSet = new HashSet<>(IndexScopedSettings.BUILT_IN_INDEX_SETTINGS);
        if (setting.length > 0) {
            settingSet.addAll(Arrays.asList(setting));
        }
        return new IndexSettings(metaData, nodeSettings, new IndexScopedSettings(Settings.EMPTY, settingSet));
    }

    public static IndexSettings newIndexSettings(Index index, Settings settings, IndexScopedSettings indexScopedSettings) {
        Settings build = Settings.builder().put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 1)
                .put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, 1)
                .put(settings)
                .build();
        IndexMetaData metaData = IndexMetaData.builder(index.getName()).settings(build).build();
        return new IndexSettings(metaData, Settings.EMPTY, indexScopedSettings);
    }

    public static IndexSettings newIndexSettings(final IndexMetaData indexMetaData, Setting<?>... setting) {
        Set<Setting<?>> settingSet = new HashSet<>(IndexScopedSettings.BUILT_IN_INDEX_SETTINGS);
        if (setting.length > 0) {
            settingSet.addAll(Arrays.asList(setting));
        }
        return new IndexSettings(indexMetaData, Settings.EMPTY, new IndexScopedSettings(Settings.EMPTY, settingSet));
    }
}
