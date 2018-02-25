package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class UniqueTokenFilterFactory extends AbstractTokenFilterFactory {

    private final boolean onlyOnSamePosition;

    UniqueTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.onlyOnSamePosition = settings.getAsBooleanLenientForPreEs6Indices(
            indexSettings.getIndexVersionCreated(), "only_on_same_position", false, deprecationLogger);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new UniqueTokenFilter(tokenStream, onlyOnSamePosition);
    }
}
