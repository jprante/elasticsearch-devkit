package org.elasticsearch.test.rest.yaml.section;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Represents an is_true assert section:
 *
 *   - is_true:  get.fields.bar
 *
 */
public class IsTrueAssertion extends Assertion {
    public static IsTrueAssertion parse(XContentParser parser) throws IOException {
        return new IsTrueAssertion(parser.getTokenLocation(), ParserUtils.parseField(parser));
    }

    private static final Logger logger = Loggers.getLogger(IsTrueAssertion.class);

    public IsTrueAssertion(XContentLocation location, String field) {
        super(location, field, true);
    }

    @Override
    protected void doAssert(Object actualValue, Object expectedValue) {
        logger.trace("assert that [{}] has a true value (field [{}])", actualValue, getField());
        String errorMessage = errorMessage();
        assertThat(errorMessage, actualValue, notNullValue());
        String actualString = actualValue.toString();
        assertThat(errorMessage, actualString, not(equalTo("")));
        assertThat(errorMessage, actualString, not(equalToIgnoringCase(Boolean.FALSE.toString())));
        assertThat(errorMessage, actualString, not(equalTo("0")));
    }

    private String errorMessage() {
        return "field [" + getField() + "] doesn't have a true value";
    }
}
