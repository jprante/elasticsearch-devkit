package org.elasticsearch.analysis.common.test;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.PatternKeywordMarkerFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.analysis.common.KeywordMarkerTokenFilterFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.testframework.index.analysis.AnalysisTestsHelper;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.testframework.ESTestCase.TestAnalysis;
import org.elasticsearch.testframework.ESTokenStreamTestCase;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;

/**
 * Tests for the {@link KeywordMarkerTokenFilterFactory} class.
 */
public class KeywordMarkerFilterFactoryTests extends ESTokenStreamTestCase {

    /**
     * Tests using a keyword set for the keyword marker filter.
     */
    public void testKeywordSet() throws IOException {
        Settings settings = Settings.builder()
            .put("index.analysis.filter.my_keyword.type", "keyword_marker")
            .put("index.analysis.filter.my_keyword.keywords", "running, sleeping")
            .put("index.analysis.analyzer.my_keyword.type", "custom")
            .put("index.analysis.analyzer.my_keyword.tokenizer", "standard")
            .put("index.analysis.analyzer.my_keyword.filter", "my_keyword, porter_stem")
            .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
            .build();
        TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(settings, new CommonAnalysisPlugin());
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("my_keyword");
        assertThat(tokenFilter, instanceOf(KeywordMarkerTokenFilterFactory.class));
        TokenStream filter = tokenFilter.create(new WhitespaceTokenizer());
        assertThat(filter, instanceOf(SetKeywordMarkerFilter.class));
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("my_keyword");
        // jogging is not part of the keywords set, so verify that its the only stemmed word
        assertAnalyzesTo(analyzer, "running jogging sleeping",
            new String[] { "running", "jog", "sleeping" });
    }

    /**
     * Tests using a regular expression pattern for the keyword marker filter.
     */
    public void testKeywordPattern() throws IOException {
        Settings settings = Settings.builder()
            .put("index.analysis.filter.my_keyword.type", "keyword_marker")
            .put("index.analysis.filter.my_keyword.keywords_pattern", "run[a-z]ing")
            .put("index.analysis.analyzer.my_keyword.type", "custom")
            .put("index.analysis.analyzer.my_keyword.tokenizer", "standard")
            .put("index.analysis.analyzer.my_keyword.filter", "my_keyword, porter_stem")
            .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
            .build();
        TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(settings, new CommonAnalysisPlugin());
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("my_keyword");
        assertThat(tokenFilter, instanceOf(KeywordMarkerTokenFilterFactory.class));
        TokenStream filter = tokenFilter.create(new WhitespaceTokenizer());
        assertThat(filter, instanceOf(PatternKeywordMarkerFilter.class));
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("my_keyword");
        // running should match the pattern, so it should not be stemmed but sleeping should
        assertAnalyzesTo(analyzer, "running sleeping", new String[] { "running", "sleep" });
    }

    /**
     * Verifies that both keywords and patterns cannot be specified together.
     */
    public void testCannotSpecifyBothKeywordsAndPattern() throws IOException {
        Settings settings = Settings.builder()
            .put("index.analysis.filter.my_keyword.type", "keyword_marker")
            .put("index.analysis.filter.my_keyword.keywords", "running")
            .put("index.analysis.filter.my_keyword.keywords_pattern", "run[a-z]ing")
            .put("index.analysis.analyzer.my_keyword.type", "custom")
            .put("index.analysis.analyzer.my_keyword.tokenizer", "standard")
            .put("index.analysis.analyzer.my_keyword.filter", "my_keyword, porter_stem")
            .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
            .build();
        IllegalArgumentException e = expectThrows(IllegalArgumentException.class,
            () -> AnalysisTestsHelper.createTestAnalysisFromSettings(settings, new CommonAnalysisPlugin()));
        assertEquals("cannot specify both `keywords_pattern` and `keywords` or `keywords_path`",
            e.getMessage());
    }
}
