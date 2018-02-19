package org.elasticsearch.test.hamcrest;

import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.hamcrest.Matcher;

/**
 * Assertions for easier handling of our custom collections,
 * for example ImmutableOpenMap
 */
public class CollectionAssertions {

    public static Matcher<ImmutableOpenMap> hasKey(final String key) {
        return new CollectionMatchers.ImmutableOpenMapHasKeyMatcher(key);
    }

    public static Matcher<ImmutableOpenMap> hasAllKeys(final String... keys) {
        return new CollectionMatchers.ImmutableOpenMapHasAllKeysMatcher(keys);
    }
}
