package org.elasticsearch.analysis.common.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.testframework.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.analysis.common.UniqueTokenFilter;
import org.elasticsearch.testframework.ESTestCase;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;

public class UniqueTokenFilterTests extends ESTestCase {

    public void testSimple() throws IOException {
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer t = new MockTokenizer(MockTokenizer.WHITESPACE, false);
                return new TokenStreamComponents(t, new UniqueTokenFilter(t));
            }
        };

        TokenStream test = analyzer.tokenStream("test", "this test with test");
        test.reset();
        CharTermAttribute termAttribute = test.addAttribute(CharTermAttribute.class);
        assertThat(test.incrementToken(), equalTo(true));
        assertThat(termAttribute.toString(), equalTo("this"));

        assertThat(test.incrementToken(), equalTo(true));
        assertThat(termAttribute.toString(), equalTo("test"));

        assertThat(test.incrementToken(), equalTo(true));
        assertThat(termAttribute.toString(), equalTo("with"));

        assertThat(test.incrementToken(), equalTo(false));
    }
}
