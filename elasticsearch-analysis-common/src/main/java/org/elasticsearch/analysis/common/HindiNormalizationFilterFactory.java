package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hi.HindiNormalizationFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.MultiTermAwareComponent;

/**
 * Factory for {@link HindiNormalizationFilter}
 */
public class HindiNormalizationFilterFactory extends AbstractTokenFilterFactory implements MultiTermAwareComponent {

    HindiNormalizationFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HindiNormalizationFilter(tokenStream);
    }

    @Override
    public Object getMultiTermComponent() {
        return this;
    }
}
