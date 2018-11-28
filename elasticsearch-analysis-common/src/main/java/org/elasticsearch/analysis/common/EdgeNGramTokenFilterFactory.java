package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.reverse.ReverseStringFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;


public class EdgeNGramTokenFilterFactory extends AbstractTokenFilterFactory {

    private final int minGram;

    private final int maxGram;

    public static final int SIDE_FRONT = 1;
    public static final int SIDE_BACK = 2;
    private final int side;

    public EdgeNGramTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.minGram = settings.getAsInt("min_gram", NGramTokenFilter.DEFAULT_MIN_NGRAM_SIZE);
        this.maxGram = settings.getAsInt("max_gram", NGramTokenFilter.DEFAULT_MAX_NGRAM_SIZE);
        this.side = parseSide(settings.get("side", "front"));
    }

    public static int parseSide(String side) {
        switch(side) {
            case "front": return SIDE_FRONT;
            case "back": return SIDE_BACK;
            default: throw new IllegalArgumentException("invalid side: " + side);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        TokenStream result = tokenStream;

        // side=BACK is not supported anymore but applying ReverseStringFilter up-front and after the token filter has the same effect
        if (side == SIDE_BACK) {
            result = new ReverseStringFilter(result);
        }

        result = new EdgeNGramTokenFilter(result, minGram, maxGram);

        // side=BACK is not supported anymore but applying ReverseStringFilter up-front and after the token filter has the same effect
        if (side == SIDE_BACK) {
            result = new ReverseStringFilter(result);
        }

        return result;
    }

    @Override
    public boolean breaksFastVectorHighlighter() {
        return true;
    }
}
