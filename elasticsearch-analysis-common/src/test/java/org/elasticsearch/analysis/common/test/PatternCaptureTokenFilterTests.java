package org.elasticsearch.analysis.common.test;

import org.elasticsearch.Version;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.analysis.common.PatternCaptureGroupTokenFilterFactory;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.IndexAnalyzers;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.testframework.ESTokenStreamTestCase;
import org.elasticsearch.testframework.IndexSettingsModule;

import static org.elasticsearch.testframework.ESTestCase.createTestAnalysis;
import static org.hamcrest.Matchers.containsString;

public class PatternCaptureTokenFilterTests extends ESTokenStreamTestCase {
    public void testPatternCaptureTokenFilter() throws Exception {
        String json = "/org/elasticsearch/analysis/common/test/pattern_capture.json";
        Settings settings = Settings.builder()
                .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir())
                .loadFromStream(json, getClass().getResourceAsStream(json), false)
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();

        IndexSettings idxSettings = IndexSettingsModule.newIndexSettings("index", settings);
        IndexAnalyzers indexAnalyzers = createTestAnalysis(idxSettings, settings, new CommonAnalysisPlugin()).indexAnalyzers;
        NamedAnalyzer analyzer1 = indexAnalyzers.get("single");

        assertTokenStreamContents(analyzer1.tokenStream("test", "foobarbaz"), new String[]{"foobarbaz","foobar","foo"});

        NamedAnalyzer analyzer2 = indexAnalyzers.get("multi");

        assertTokenStreamContents(analyzer2.tokenStream("test", "abc123def"), new String[]{"abc123def","abc","123","def"});

        NamedAnalyzer analyzer3 = indexAnalyzers.get("preserve");

        assertTokenStreamContents(analyzer3.tokenStream("test", "foobarbaz"), new String[]{"foobar","foo"});
    }

    public void testNoPatterns() {
        try {
            new PatternCaptureGroupTokenFilterFactory(IndexSettingsModule.newIndexSettings("test", Settings.EMPTY), null,
                    "pattern_capture", Settings.builder().put("pattern", "foobar").build());
            fail ("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("required setting 'patterns' is missing"));
        }
    }

}
