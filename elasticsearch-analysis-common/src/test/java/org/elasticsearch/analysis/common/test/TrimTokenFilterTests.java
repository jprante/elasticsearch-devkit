package org.elasticsearch.analysis.common.test;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.testframework.index.analysis.AnalysisTestsHelper;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.ESTokenStreamTestCase;

import java.io.IOException;

public class TrimTokenFilterTests extends ESTokenStreamTestCase {

    public void testNormalizer() throws IOException {
        Settings settings = Settings.builder()
            .putList("index.analysis.normalizer.my_normalizer.filter", "trim")
            .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
            .build();
        ESTestCase.TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(settings, new CommonAnalysisPlugin());
        assertNull(analysis.indexAnalyzers.get("my_normalizer"));
        NamedAnalyzer normalizer = analysis.indexAnalyzers.getNormalizer("my_normalizer");
        assertNotNull(normalizer);
        assertEquals("my_normalizer", normalizer.name());
        assertTokenStreamContents(normalizer.tokenStream("foo", "  bar  "), new String[] {"bar"});
        assertEquals(new BytesRef("bar"), normalizer.normalize("foo", "  bar  "));
    }

}
