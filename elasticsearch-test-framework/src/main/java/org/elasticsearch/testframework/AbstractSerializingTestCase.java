package org.elasticsearch.testframework;

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.function.Predicate;

public abstract class AbstractSerializingTestCase<T extends ToXContent & Writeable> extends AbstractWireSerializingTestCase<T> {

    /**
     * Generic test that creates new instance from the test instance and checks
     * both for equality and asserts equality on the two instances.
     */
    public final void testFromXContent() throws IOException {
        AbstractXContentTestCase.testFromXContent(NUMBER_OF_TEST_RUNS, this::createTestInstance, supportsUnknownFields(),
                getShuffleFieldsExceptions(), getRandomFieldsExcludeFilter(), this::createParser, this::doParseInstance,
                this::assertEqualInstances, true);
    }

    /**
      * Parses to a new instance using the provided {@link XContentParser}
      */
    protected abstract T doParseInstance(XContentParser parser) throws IOException;

    /**
      * Indicates whether the parser supports unknown fields or not. In case it does, such behaviour will be tested by
      * inserting random fields before parsing and checking that they don't make parsing fail.
      */
    protected boolean supportsUnknownFields() {
        return false;
    }

    protected Predicate<String> getRandomFieldsExcludeFilter() {
        return field -> false;
    }

    /**
     * Fields that have to be ignored when shuffling as part of testFromXContent
     */
    protected String[] getShuffleFieldsExceptions() {
        return Strings.EMPTY_ARRAY;
    }
}
