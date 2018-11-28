package org.elasticsearch.testframework.index.analysis;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class MyFilterTokenFilterFactory extends AbstractTokenFilterFactory {

    public MyFilterTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, Settings.Builder.EMPTY_SETTINGS);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new StopFilter(tokenStream, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }
}
