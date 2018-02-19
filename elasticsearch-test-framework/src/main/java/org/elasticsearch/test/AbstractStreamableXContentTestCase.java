package org.elasticsearch.test;

import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertToXContentEquivalent;

public abstract class AbstractStreamableXContentTestCase<T extends ToXContent & Streamable> extends AbstractStreamableTestCase<T> {

    /**
     * Generic test that creates new instance from the test instance and checks
     * both for equality and asserts equality on the two queries.
     */
    public void testFromXContent() throws IOException {
        for (int runs = 0; runs < NUMBER_OF_TEST_RUNS; runs++) {
            T testInstance = createTestInstance();
            XContentType xContentType = randomFrom(XContentType.values());
            BytesReference shuffled = toShuffledXContent(testInstance, xContentType, ToXContent.EMPTY_PARAMS, false);
            BytesReference withRandomFields;
            if (supportsUnknownFields()) {
                // we add a few random fields to check that parser is lenient on new fields
                withRandomFields = XContentTestUtils.insertRandomFields(xContentType, shuffled, null, random());
            } else {
                withRandomFields = shuffled;
            }
            XContentParser parser = createParser(XContentFactory.xContent(xContentType), withRandomFields);
            T parsed = parseInstance(parser);
            T expected = getExpectedFromXContent(testInstance);
            assertNotSame(expected, parsed);
            assertEquals(expected, parsed);
            assertEquals(expected.hashCode(), parsed.hashCode());
            assertToXContentEquivalent(shuffled, XContentHelper.toXContent(parsed, xContentType, false), xContentType);
        }
    }

    /**
     * Returns the expected parsed object given the test object that the parser will be fed with.
     * Useful in cases some fields are not written as part of toXContent, hence not parsed back.
     */
    protected T getExpectedFromXContent(T testInstance) {
        return testInstance;
    }

    /**
     * Indicates whether the parser supports unknown fields or not. In case it does, such behaviour will be tested by
     * inserting random fields before parsing and checking that they don't make parsing fail.
     */
    protected boolean supportsUnknownFields() {
        return true;
    }

    private T parseInstance(XContentParser parser) throws IOException {
        T parsedInstance = doParseInstance(parser);
        assertNull(parser.nextToken());
        return parsedInstance;
    }

    /**
     * Parses to a new instance using the provided {@link XContentParser}
     */
    protected abstract T doParseInstance(XContentParser parser);
}
