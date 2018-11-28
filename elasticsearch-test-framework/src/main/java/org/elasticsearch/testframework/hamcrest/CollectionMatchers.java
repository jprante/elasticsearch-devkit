
package org.elasticsearch.testframework.hamcrest;

import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.List;

/**
 * Matchers for easier handling of our custom collections,
 * for example ImmutableOpenMap
 */
public class CollectionMatchers {

    public static class ImmutableOpenMapHasKeyMatcher extends TypeSafeMatcher<ImmutableOpenMap> {

        private final String key;

        public ImmutableOpenMapHasKeyMatcher(String key) {
            this.key = key;
        }

        @Override
        protected boolean matchesSafely(ImmutableOpenMap item) {
            return item.containsKey(key);
        }

        @Override
        public void describeMismatchSafely(final ImmutableOpenMap map, final Description mismatchDescription) {
            if (map.size() == 0) {
                mismatchDescription.appendText("was empty");
            } else {
                mismatchDescription.appendText(" was ").appendValue(map);
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("ImmutableOpenMap should contain key " + key);
        }
    }

    public static class ImmutableOpenMapHasAllKeysMatcher extends TypeSafeMatcher<ImmutableOpenMap> {

        private final List<String> keys;
        private String missingKey;

        public ImmutableOpenMapHasAllKeysMatcher(final String... keys) {
            this.keys = Arrays.asList(keys);
        }

        @Override
        protected boolean matchesSafely(ImmutableOpenMap item) {
            for (String key: keys) {
                if (!item.containsKey(key)) {
                    missingKey = key;
                    return false;
                }
            }

            return true;
        }

        @Override
        public void describeMismatchSafely(final ImmutableOpenMap map, final Description mismatchDescription) {
            if (map.size() == 0) {
                mismatchDescription.appendText("was empty");
            } else {
                mismatchDescription.appendText("was ").appendValue(map.keys());
            }
        }

        @Override
        public void describeTo(Description description) {
            description
                .appendText("ImmutableOpenMap should contain all keys ")
                .appendValue(keys)
                .appendText(", but key [")
                .appendValue(missingKey)
                .appendText("] is missing");
        }
    }

}
