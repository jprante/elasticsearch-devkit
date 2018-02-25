package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.elasticsearch.common.regex.Regex;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

import java.util.regex.Pattern;

public class PatternReplaceTokenFilterFactory extends AbstractTokenFilterFactory {

    private final Pattern pattern;
    private final String replacement;
    private final boolean all;

    public PatternReplaceTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);

        String sPattern = settings.get("pattern", null);
        if (sPattern == null) {
            throw new IllegalArgumentException("pattern is missing for [" + name + "] token filter of type 'pattern_replace'");
        }
        this.pattern = Regex.compile(sPattern, settings.get("flags"));
        this.replacement = settings.get("replacement", "");
        this.all = settings.getAsBooleanLenientForPreEs6Indices(indexSettings.getIndexVersionCreated(), "all", true, deprecationLogger);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new PatternReplaceFilter(tokenStream, pattern, replacement, all);
    }
}
