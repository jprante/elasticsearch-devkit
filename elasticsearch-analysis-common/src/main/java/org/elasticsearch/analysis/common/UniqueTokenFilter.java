package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

/**
 * A token filter that generates unique tokens. Can remove unique tokens only on the same
 * position increments as well.
 */
public class UniqueTokenFilter extends TokenFilter {

    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = addAttribute(PositionIncrementAttribute.class);

    private final CharArraySet previous = new CharArraySet(8, false);
    private final boolean onlyOnSamePosition;

    public UniqueTokenFilter(TokenStream in) {
        this(in, false);
    }

    public UniqueTokenFilter(TokenStream in, boolean onlyOnSamePosition) {
        super(in);
        this.onlyOnSamePosition = onlyOnSamePosition;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            final char term[] = termAttribute.buffer();
            final int length = termAttribute.length();

            boolean duplicate;
            if (onlyOnSamePosition) {
                final int posIncrement = posIncAttribute.getPositionIncrement();
                if (posIncrement > 0) {
                    previous.clear();
                }

                duplicate = (posIncrement == 0 && previous.contains(term, 0, length));
            } else {
                duplicate = previous.contains(term, 0, length);
            }

            // clone the term, and add to the set of seen terms.
            char saved[] = new char[length];
            System.arraycopy(term, 0, saved, 0, length);
            previous.add(saved);

            if (!duplicate) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final void reset() throws IOException {
        super.reset();
        previous.clear();
    }
}


