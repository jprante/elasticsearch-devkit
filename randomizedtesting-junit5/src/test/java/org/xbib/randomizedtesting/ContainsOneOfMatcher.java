package org.xbib.randomizedtesting;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Array;

public class ContainsOneOfMatcher<T> extends BaseMatcher<T> {
    private final Object expected;

    public ContainsOneOfMatcher(Object expected) {
        this.expected = expected;
    }

    public static <T> ContainsOneOfMatcher<T> containsOneOf(char[] chars) {
        return new ContainsOneOfMatcher<>(chars);
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
            String actualString = (String) actual;
            for (int i = 0; i < Array.getLength(expected); i++) {
                String s = String.valueOf(Array.get(expected, i));
                if (actualString.contains(s)) return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expected);
    }
}
