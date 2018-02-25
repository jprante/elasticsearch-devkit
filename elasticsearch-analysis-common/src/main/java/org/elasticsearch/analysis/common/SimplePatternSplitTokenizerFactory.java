package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.pattern.SimplePatternSplitTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;

public class SimplePatternSplitTokenizerFactory extends AbstractTokenizerFactory {

    private final String pattern;

    public SimplePatternSplitTokenizerFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);

        pattern = settings.get("pattern", "");
    }

    @Override
    public Tokenizer create() {
        return new SimplePatternSplitTokenizer(pattern);
    }
}
