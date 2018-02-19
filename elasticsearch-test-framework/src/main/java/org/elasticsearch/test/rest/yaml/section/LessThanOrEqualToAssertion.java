package org.elasticsearch.test.rest.yaml.section;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Represents a lte assert section:
 *
 *   - lte:     { fields._ttl: 0 }
 */
public class LessThanOrEqualToAssertion  extends Assertion {
    public static LessThanOrEqualToAssertion parse(XContentParser parser) throws IOException {
        XContentLocation location = parser.getTokenLocation();
        Tuple<String,Object> stringObjectTuple = ParserUtils.parseTuple(parser);
        if (!(stringObjectTuple.v2() instanceof Comparable)) {
            throw new IllegalArgumentException("lte section can only be used with objects that support natural ordering, found "
                    + stringObjectTuple.v2().getClass().getSimpleName());
        }
        return new LessThanOrEqualToAssertion(location, stringObjectTuple.v1(), stringObjectTuple.v2());
    }

    private static final Logger logger = Loggers.getLogger(LessThanOrEqualToAssertion.class);

    public LessThanOrEqualToAssertion(XContentLocation location, String field, Object expectedValue) {
        super(location, field, expectedValue);
    }

    @Override
    protected void doAssert(Object actualValue, Object expectedValue) {
        logger.trace("assert that [{}] is less than or equal to [{}] (field: [{}])", actualValue, expectedValue, getField());
        assertThat("value of [" + getField() + "] is not comparable (got [" + safeClass(actualValue) + "])",
                actualValue, instanceOf(Comparable.class));
        assertThat("expected value of [" + getField() + "] is not comparable (got [" + expectedValue.getClass() + "])",
                expectedValue, instanceOf(Comparable.class));
        try {
            assertThat(errorMessage(), (Comparable) actualValue, lessThanOrEqualTo((Comparable) expectedValue));
        } catch (ClassCastException e) {
            fail("cast error while checking (" + errorMessage() + "): " + e);
        }
    }

    private String errorMessage() {
        return "field [" + getField() + "] is not less than or equal to [" + getExpectedValue() + "]";
    }
}
