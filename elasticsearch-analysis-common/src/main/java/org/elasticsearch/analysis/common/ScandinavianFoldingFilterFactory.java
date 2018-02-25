package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ScandinavianFoldingFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.MultiTermAwareComponent;

/**
 * Factory for {@link ScandinavianFoldingFilter}
 */
public class ScandinavianFoldingFilterFactory extends AbstractTokenFilterFactory implements MultiTermAwareComponent {

    ScandinavianFoldingFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new ScandinavianFoldingFilter(tokenStream);
    }

    @Override
    public Object getMultiTermComponent() {
        return this;
    }
}
