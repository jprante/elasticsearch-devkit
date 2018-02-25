package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;
import org.elasticsearch.index.analysis.MultiTermAwareComponent;

public class ElisionTokenFilterFactory extends AbstractTokenFilterFactory implements MultiTermAwareComponent {

    private final CharArraySet articles;

    ElisionTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.articles = Analysis.parseArticles(env, indexSettings.getIndexVersionCreated(), settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new ElisionFilter(tokenStream, articles);
    }

    @Override
    public Object getMultiTermComponent() {
        return this;
    }
}
