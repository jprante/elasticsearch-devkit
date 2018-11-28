package org.elasticsearch.testframework.search.aggregations.metrics;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.testframework.ESIntegTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@ESIntegTestCase.SuiteScopeTestCase
public abstract class AbstractNumericTestCase extends ESIntegTestCase {
    protected static long minValue, maxValue, minValues, maxValues;

    @Override
    public void setupSuiteScopeCluster() throws Exception {
        createIndex("idx");
        createIndex("idx_unmapped");

        List<IndexRequestBuilder> builders = new ArrayList<>();

        final int numDocs = 10;
        for (int i = 0; i < numDocs; i++) { // TODO randomize the size and the params in here?
            builders.add(client().prepareIndex("idx", "type", String.valueOf(i)).setSource(jsonBuilder()
                    .startObject()
                    .field("value", i+1)
                    .startArray("values").value(i+2).value(i+3).endArray()
                    .endObject()));
        }
        minValue = 1;
        minValues = 2;
        maxValue = numDocs;
        maxValues = numDocs + 2;
        indexRandom(true, builders);

        // creating an index to test the empty buckets functionality. The way it works is by indexing
        // two docs {value: 0} and {value : 2}, then building a histogram agg with interval 1 and with empty
        // buckets computed.. the empty bucket is the one associated with key "1". then each test will have
        // to check that this bucket exists with the appropriate sub aggregations.
        prepareCreate("empty_bucket_idx").addMapping("type", "value", "type=integer").execute().actionGet();
        builders = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            builders.add(client().prepareIndex("empty_bucket_idx", "type", String.valueOf(i)).setSource(jsonBuilder()
                    .startObject()
                    .field("value", i*2)
                    .endObject()));
        }
        indexRandom(true, builders);
        ensureSearchable();
    }

    public abstract void testEmptyAggregation() throws Exception;

    public abstract void testUnmapped() throws Exception;

    public abstract void testSingleValuedField() throws Exception;

    public abstract void testSingleValuedFieldGetProperty() throws Exception;

    public abstract void testSingleValuedFieldPartiallyUnmapped() throws Exception;

    public abstract void testSingleValuedFieldWithValueScript() throws Exception;

    public abstract void testSingleValuedFieldWithValueScriptWithParams() throws Exception;

    public abstract void testMultiValuedField() throws Exception;

    public abstract void testMultiValuedFieldWithValueScript() throws Exception;

    public abstract void testMultiValuedFieldWithValueScriptWithParams() throws Exception;

    public abstract void testScriptSingleValued() throws Exception;

    public abstract void testScriptSingleValuedWithParams() throws Exception;

    public abstract void testScriptMultiValued() throws Exception;

    public abstract void testScriptMultiValuedWithParams() throws Exception;

    public abstract void testOrderByEmptyAggregation() throws Exception;
}
