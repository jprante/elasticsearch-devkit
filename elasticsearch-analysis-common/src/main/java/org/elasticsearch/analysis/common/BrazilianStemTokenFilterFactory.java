package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;

public class BrazilianStemTokenFilterFactory extends AbstractTokenFilterFactory {

    private final CharArraySet exclusions;

    BrazilianStemTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.exclusions = Analysis.parseStemExclusion(settings, CharArraySet.EMPTY_SET);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new BrazilianStemFilter(new SetKeywordMarkerFilter(tokenStream, exclusions));
    }
}
