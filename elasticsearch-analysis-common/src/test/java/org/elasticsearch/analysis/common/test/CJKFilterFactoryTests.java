package org.elasticsearch.analysis.common.test;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.DisableGraphAttribute;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.testframework.index.analysis.AnalysisTestsHelper;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.ESTokenStreamTestCase;
import org.junit.Before;

import java.io.IOException;
import java.io.StringReader;

public class CJKFilterFactoryTests extends ESTokenStreamTestCase {
    private static final String RESOURCE = "/org/elasticsearch/analysis/common/test/cjk_analysis.json";

    private ESTestCase.TestAnalysis analysis;

    @Before
    public void setup() throws IOException {
        analysis = AnalysisTestsHelper.createTestAnalysisFromClassPath(getClass(),
                createTempDir(), RESOURCE, new CommonAnalysisPlugin());
    }

    public void testDefault() throws IOException {
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("cjk_bigram");
        String source = "多くの学生が試験に落ちた。";
        String[] expected = new String[]{"多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた" };
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testNoFlags() throws IOException {
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("cjk_no_flags");
        String source = "多くの学生が試験に落ちた。";
        String[] expected = new String[]{"多く", "くの", "の学", "学生", "生が", "が試", "試験", "験に", "に落", "落ち", "ちた" };
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testHanOnly() throws IOException {
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("cjk_han_only");
        String source = "多くの学生が試験に落ちた。";
        String[] expected = new String[]{"多", "く", "の",  "学生", "が",  "試験", "に",  "落", "ち", "た"  };
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testHanUnigramOnly() throws IOException {
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("cjk_han_unigram_only");
        String source = "多くの学生が試験に落ちた。";
        String[] expected = new String[]{"多", "く", "の",  "学", "学生", "生", "が",  "試", "試験", "験", "に",  "落", "ち", "た"  };
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testDisableGraph() throws IOException {
        TokenFilterFactory allFlagsFactory = analysis.tokenFilter.get("cjk_all_flags");
        TokenFilterFactory hanOnlyFactory = analysis.tokenFilter.get("cjk_han_only");

        String source = "多くの学生が試験に落ちた。";
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(source));
        try (TokenStream tokenStream = allFlagsFactory.create(tokenizer)) {
            // This config outputs different size of ngrams so graph analysis is disabled
            assertTrue(tokenStream.hasAttribute(DisableGraphAttribute.class));
        }

        tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(source));
        try (TokenStream tokenStream = hanOnlyFactory.create(tokenizer)) {
            // This config uses only bigrams so graph analysis is enabled
            assertFalse(tokenStream.hasAttribute(DisableGraphAttribute.class));
        }
    }
}
