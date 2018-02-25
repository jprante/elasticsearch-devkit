package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cz.CzechStemFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class CzechStemTokenFilterFactory extends AbstractTokenFilterFactory {

    CzechStemTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new CzechStemFilter(tokenStream);
    }
}
