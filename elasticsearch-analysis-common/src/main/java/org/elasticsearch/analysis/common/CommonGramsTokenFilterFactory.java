package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.commongrams.CommonGramsQueryFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;

public class CommonGramsTokenFilterFactory extends AbstractTokenFilterFactory {

    private final CharArraySet words;

    private final boolean ignoreCase;

    private final boolean queryMode;

    CommonGramsTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.ignoreCase = settings.getAsBooleanLenientForPreEs6Indices(indexSettings.getIndexVersionCreated(),
                "ignore_case", false, deprecationLogger);
        this.queryMode = settings.getAsBooleanLenientForPreEs6Indices(indexSettings.getIndexVersionCreated(),
                "query_mode", false, deprecationLogger);
        this.words = Analysis.parseCommonWords(env, settings, null, ignoreCase);

        if (this.words == null) {
            throw new IllegalArgumentException(
                    "missing or empty [common_words] or [common_words_path] configuration for common_grams token filter");
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        CommonGramsFilter filter = new CommonGramsFilter(tokenStream, words);
        if (queryMode) {
            return new CommonGramsQueryFilter(filter);
        } else {
            return filter;
        }
    }
}

