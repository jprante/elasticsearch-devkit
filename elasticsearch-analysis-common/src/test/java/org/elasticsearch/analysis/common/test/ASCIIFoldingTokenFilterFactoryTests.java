package org.elasticsearch.analysis.common.test;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.testframework.index.analysis.AnalysisTestsHelper;
import org.elasticsearch.index.analysis.MultiTermAwareComponent;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.ESTokenStreamTestCase;

import java.io.IOException;
import java.io.StringReader;

public class ASCIIFoldingTokenFilterFactoryTests extends ESTokenStreamTestCase {
    public void testDefault() throws IOException {
        ESTestCase.TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(
                Settings.builder()
                    .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
                    .put("index.analysis.filter.my_ascii_folding.type", "asciifolding")
                    .build(),
                new CommonAnalysisPlugin());
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("my_ascii_folding");
        String source = "Ansprüche";
        String[] expected = new String[]{"Anspruche"};
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testPreserveOriginal() throws IOException {
        ESTestCase.TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(
                Settings.builder()
                    .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
                    .put("index.analysis.filter.my_ascii_folding.type", "asciifolding")
                    .put("index.analysis.filter.my_ascii_folding.preserve_original", true)
                    .build(),
                new CommonAnalysisPlugin());
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("my_ascii_folding");
        String source = "Ansprüche";
        String[] expected = new String[]{"Anspruche", "Ansprüche"};
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);

        // but the multi-term aware component still emits a single token
        tokenFilter = (TokenFilterFactory) ((MultiTermAwareComponent) tokenFilter)
                .getMultiTermComponent();
        tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader(source));
        expected = new String[]{"Anspruche"};
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }
}
