package org.elasticsearch.analysis.common.test;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.testframework.index.analysis.AnalysisTestsHelper;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.testframework.ESTestCase;

import java.io.IOException;
import java.io.StringReader;

public class WordDelimiterGraphTokenFilterFactoryTests
        extends BaseWordDelimiterTokenFilterFactoryTestCase {
    public WordDelimiterGraphTokenFilterFactoryTests() {
        super("word_delimiter_graph");
    }

    public void testMultiTerms() throws IOException {
        ESTestCase.TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(
                Settings.builder()
                    .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
                    .put("index.analysis.filter.my_word_delimiter.type", type)
                    .put("index.analysis.filter.my_word_delimiter.catenate_all", "true")
                    .put("index.analysis.filter.my_word_delimiter.preserve_original", "true")
                    .build(),
                new CommonAnalysisPlugin());

        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("my_word_delimiter");
        String source = "PowerShot 500-42 wi-fi wi-fi-4000 j2se O'Neil's";
        String[] expected = new String[] { "PowerShot", "PowerShot", "Power", "Shot", "50042",
                "500-42", "500", "42", "wifi", "wi-fi", "wi", "fi", "wifi4000", "wi-fi-4000", "wi",
                "fi", "4000", "j2se", "j2se", "j", "2", "se", "ONeil", "O'Neil's", "O", "Neil" };
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader(source));
        int[] expectedIncr = new int[] { 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0,
                1, 1, 1, 0, 0, 1 };
        int[] expectedPosLen = new int[] { 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 3, 3, 1, 1, 1, 3, 3,
                1, 1, 1, 2, 2, 1, 1 };
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected, null, null, null,
                expectedIncr, expectedPosLen, null);
    }

    /**
     * Correct offset order when doing both parts and concatenation: PowerShot is a synonym of Power
     */
    public void testPartsAndCatenate() throws IOException {
        ESTestCase.TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(
                Settings.builder()
                    .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
                    .put("index.analysis.filter.my_word_delimiter.type", type)
                    .put("index.analysis.filter.my_word_delimiter.catenate_words", "true")
                    .put("index.analysis.filter.my_word_delimiter.generate_word_parts", "true")
                    .build(),
                new CommonAnalysisPlugin());
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("my_word_delimiter");
        String source = "PowerShot";
        int[] expectedIncr = new int[]{1, 0, 1};
        int[] expectedPosLen = new int[]{2, 1, 1};
        String[] expected = new String[]{"PowerShot", "Power", "Shot" };
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected, null, null, null,
            expectedIncr, expectedPosLen, null);
    }
}
