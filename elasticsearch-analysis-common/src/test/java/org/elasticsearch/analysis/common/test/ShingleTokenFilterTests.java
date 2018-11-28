package org.elasticsearch.analysis.common.test;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.DisableGraphAttribute;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.testframework.index.analysis.AnalysisTestsHelper;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.ESTokenStreamTestCase;

import java.io.StringReader;

public class ShingleTokenFilterTests extends ESTokenStreamTestCase {
    public void testPreConfiguredShingleFilterDisableGraphAttribute() throws Exception {
        ESTestCase.TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(
            Settings.builder()
                .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
                .put("index.analysis.filter.my_ascii_folding.type", "asciifolding")
                .build(),
            new CommonAnalysisPlugin());
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("shingle");
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader("this is a test"));
        TokenStream tokenStream = tokenFilter.create(tokenizer);
        assertTrue(tokenStream.hasAttribute(DisableGraphAttribute.class));
    }
}
