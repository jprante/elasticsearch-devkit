package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;


/**
 * Uses the {@link org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter} to decompound tokens using a dictionary.
 *
 * @see org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter
 */
public class DictionaryCompoundWordTokenFilterFactory extends AbstractCompoundWordTokenFilterFactory {

    DictionaryCompoundWordTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, env, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new DictionaryCompoundWordTokenFilter(tokenStream, wordList, minWordSize,
                                                     minSubwordSize, maxSubwordSize, onlyLongestMatch);
    }
}
