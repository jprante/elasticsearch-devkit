package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;

/**
 * Contains the common configuration settings between subclasses of this class.
 */
public abstract class AbstractCompoundWordTokenFilterFactory extends AbstractTokenFilterFactory {

    protected final int minWordSize;
    protected final int minSubwordSize;
    protected final int maxSubwordSize;
    protected final boolean onlyLongestMatch;
    protected final CharArraySet wordList;

    protected AbstractCompoundWordTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        minWordSize = settings.getAsInt("min_word_size", CompoundWordTokenFilterBase.DEFAULT_MIN_WORD_SIZE);
        minSubwordSize = settings.getAsInt("min_subword_size", CompoundWordTokenFilterBase.DEFAULT_MIN_SUBWORD_SIZE);
        maxSubwordSize = settings.getAsInt("max_subword_size", CompoundWordTokenFilterBase.DEFAULT_MAX_SUBWORD_SIZE);
        onlyLongestMatch = settings
            .getAsBooleanLenientForPreEs6Indices(indexSettings.getIndexVersionCreated(), "only_longest_match", false, deprecationLogger);
        wordList = Analysis.getWordSet(env, indexSettings.getIndexVersionCreated(), settings, "word_list");
        if (wordList == null) {
            throw new IllegalArgumentException("word_list must be provided for [" + name + "], either as a path to a file, or directly");
        }
    }
}
