package org.elasticsearch.test.hamcrest;

import org.elasticsearch.search.SearchHit;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ElasticsearchMatchers {

    public static class SearchHitHasIdMatcher extends TypeSafeMatcher<SearchHit> {
        private String id;

        public SearchHitHasIdMatcher(String id) {
            this.id = id;
        }

        @Override
        protected boolean matchesSafely(SearchHit searchHit) {
            return searchHit.getId().equals(id);
        }

        @Override
        public void describeMismatchSafely(final SearchHit searchHit, final Description mismatchDescription) {
            mismatchDescription.appendText(" was ").appendValue(searchHit.getId());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("searchHit id should be ").appendValue(id);
        }
    }

    public static class SearchHitHasTypeMatcher extends TypeSafeMatcher<SearchHit> {
        private String type;

        public SearchHitHasTypeMatcher(String type) {
            this.type = type;
        }

        @Override
        public boolean matchesSafely(final SearchHit searchHit) {
            return searchHit.getType().equals(type);
        }

        @Override
        public void describeMismatchSafely(final SearchHit searchHit, final Description mismatchDescription) {
            mismatchDescription.appendText(" was ").appendValue(searchHit.getType());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("searchHit type should be ").appendValue(type);
        }
    }

    public static class SearchHitHasIndexMatcher extends TypeSafeMatcher<SearchHit> {
        private String index;

        public SearchHitHasIndexMatcher(String index) {
            this.index = index;
        }

        @Override
        public boolean matchesSafely(final SearchHit searchHit) {
            return searchHit.getIndex().equals(index);
        }

        @Override
        public void describeMismatchSafely(final SearchHit searchHit, final Description mismatchDescription) {
            mismatchDescription.appendText(" was ").appendValue(searchHit.getIndex());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("searchHit index should be ").appendValue(index);
        }
    }

    public static class SearchHitHasScoreMatcher extends TypeSafeMatcher<SearchHit> {
        private float score;

        public SearchHitHasScoreMatcher(float score) {
            this.score = score;
        }

        @Override
        protected boolean matchesSafely(SearchHit searchHit) {
            return searchHit.getScore() == score;
        }

        @Override
        public void describeMismatchSafely(final SearchHit searchHit, final Description mismatchDescription) {
            mismatchDescription.appendText(" was ").appendValue(searchHit.getScore());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("searchHit score should be ").appendValue(score);
        }
    }
}
