package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;

import java.io.Reader;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.elasticsearch.common.util.set.Sets.newHashSet;

public class HtmlStripCharFilterFactory extends AbstractCharFilterFactory {
    private final Set<String> escapedTags;

    HtmlStripCharFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name);
        List<String> escapedTags = settings.getAsList("escaped_tags");
        if (escapedTags.size() > 0) {
            this.escapedTags = unmodifiableSet(newHashSet(escapedTags));
        } else {
            this.escapedTags = null;
        }
    }

    @Override
    public Reader create(Reader tokenStream) {
        return new HTMLStripCharFilter(tokenStream, escapedTags);
    }
}
