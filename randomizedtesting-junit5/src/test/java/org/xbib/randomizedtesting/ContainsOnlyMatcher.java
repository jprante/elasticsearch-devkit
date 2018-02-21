package org.xbib.randomizedtesting;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ContainsOnlyMatcher<T> extends BaseMatcher<T> {

    private final char[] expected;

    public ContainsOnlyMatcher(char... expected) {
        this.expected = expected;
    }

    public static <T> ContainsOnlyMatcher<T> containsOnly(char... chars) {
        return new ContainsOnlyMatcher<>(chars);
    }

    private static boolean isArray(Object o) {
        return o.getClass().isArray();
    }

    @Override
    public boolean matches(Object actual) {
        if (actual == null) {
            return false;
        }
        if (expected != null && actual instanceof String && isArray(expected)) {
            String expectedVocabulary = String.valueOf(expected);
            for (char actualNextChar : ((String) actual).toCharArray()) {
                if (!expectedVocabulary.contains(String.valueOf(actualNextChar))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expected);
    }
}
