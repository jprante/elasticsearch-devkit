package org.elasticsearch.index.analysis;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.test.IndexSettingsModule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class AnalysisTestsHelper {

    public static ESTestCase.TestAnalysis createTestAnalysisFromClassPath(final Path baseDir,
                                                                          final String resource,
                                                                          final AnalysisPlugin... plugins) throws IOException {
        final Settings settings = Settings.builder()
                .loadFromStream(resource, AnalysisTestsHelper.class.getResourceAsStream(resource), false)
                .put(Environment.PATH_HOME_SETTING.getKey(), baseDir.toString())
                .build();

        return createTestAnalysisFromSettings(settings, plugins);
    }

    public static ESTestCase.TestAnalysis createTestAnalysisFromSettings(
            final Settings settings, final AnalysisPlugin... plugins) throws IOException {
        return createTestAnalysisFromSettings(settings, null, plugins);
    }

    public static ESTestCase.TestAnalysis createTestAnalysisFromSettings(
            final Settings settings,
            final Path configPath,
            final AnalysisPlugin... plugins) throws IOException {
        final Settings actualSettings;
        if (settings.get(IndexMetaData.SETTING_VERSION_CREATED) == null) {
            actualSettings = Settings.builder().put(settings).put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT).build();
        } else {
            actualSettings = settings;
        }
        final IndexSettings indexSettings = IndexSettingsModule.newIndexSettings("test", actualSettings);
        final AnalysisRegistry analysisRegistry =
                new AnalysisModule(new Environment(actualSettings, configPath), Arrays.asList(plugins)).getAnalysisRegistry();
        return new ESTestCase.TestAnalysis(analysisRegistry.build(indexSettings),
                analysisRegistry.buildTokenFilterFactories(indexSettings),
                analysisRegistry.buildTokenizerFactories(indexSettings),
                analysisRegistry.buildCharFilterFactories(indexSettings));
    }

}
