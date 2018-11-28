package org.elasticsearch.testframework.test.search;

import org.apache.lucene.search.Query;
import org.elasticsearch.Version;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.lucene.search.Queries;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.search.SearchContext;
import org.elasticsearch.search.SearchShardTarget;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.TestSearchContext;
import org.elasticsearch.testframework.search.MockSearchService;

public class MockSearchServiceTests extends ESTestCase {
    public static final IndexMetaData EMPTY_INDEX_METADATA = IndexMetaData.builder("")
        .settings(Settings.builder().put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT))
        .numberOfShards(1).numberOfReplicas(0).build();

    public void testAssertNoInFlightContext() {
        final long nowInMillis = randomNonNegativeLong();
        SearchContext s = new TestSearchContext(new QueryShardContext(0,
            new IndexSettings(EMPTY_INDEX_METADATA, Settings.EMPTY), null, null, null, null, null, xContentRegistry(),
            writableRegistry(), null, null, () -> nowInMillis, null)) {

            @Override
            public SearchShardTarget shardTarget() {
                return new SearchShardTarget("node", new Index("idx", "ignored"), 0, null);
            }

            @Override
            public SearchType searchType() {
                return SearchType.DEFAULT;
            }

            @Override
            public Query query() {
                return Queries.newMatchAllQuery();
            }
        };
        MockSearchService.addActiveContext(s);
        try {
            Throwable e = expectThrows(AssertionError.class, () -> MockSearchService.assertNoInFlightContext());
            assertEquals("There are still [1] in-flight contexts. The first one's creation site is listed as the cause of this exception.",
                    e.getMessage());
            e = e.getCause();
            // The next line with throw an exception if the date looks wrong
            assertEquals("[node][idx][0] query=[*:*]", e.getMessage());
            assertEquals(MockSearchService.class.getName(), e.getStackTrace()[0].getClassName());
            assertEquals(MockSearchServiceTests.class.getName(), e.getStackTrace()[1].getClassName());
        } finally {
            MockSearchService.removeActiveContext(s);
        }
    }
}
