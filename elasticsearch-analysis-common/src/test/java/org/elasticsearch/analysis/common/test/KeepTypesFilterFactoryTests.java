package org.elasticsearch.analysis.common.test;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.analysis.common.KeepTypesFilterFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.testframework.index.analysis.AnalysisTestsHelper;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.ESTokenStreamTestCase;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.Matchers.instanceOf;

public class KeepTypesFilterFactoryTests extends ESTokenStreamTestCase {
    public void testKeepTypes() throws IOException {
        Settings settings = Settings.builder()
                .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString())
                .put("index.analysis.filter.keep_numbers.type", "keep_types")
                .putList("index.analysis.filter.keep_numbers.types", new String[] {"<NUM>", "<SOMETHINGELSE>"})
                .build();
        ESTestCase.TestAnalysis analysis = AnalysisTestsHelper.createTestAnalysisFromSettings(settings, new CommonAnalysisPlugin());
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("keep_numbers");
        assertThat(tokenFilter, instanceOf(KeepTypesFilterFactory.class));
        String source = "Hello 123 world";
        String[] expected = new String[]{"123"};
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(source));
        assertTokenStreamContents(tokenFilter.create(tokenizer), expected, new int[]{2});
    }
}
