package org.elasticsearch.testframework.rest.yaml.section;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.testframework.rest.yaml.ClientYamlTestExecutionContext;

import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a test fragment that can be executed (e.g. api call, assertion)
 */
public interface ExecutableSection {
    /**
     * {@link NamedXContentRegistry} needed in the {@link XContentParser} before calling {@link ExecutableSection#parse(XContentParser)}.
     */
    NamedXContentRegistry XCONTENT_REGISTRY = new NamedXContentRegistry(Arrays.asList(
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("do"), DoSection::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("set"), SetSection::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("match"), MatchAssertion::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("is_true"), IsTrueAssertion::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("is_false"), IsFalseAssertion::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("gt"), GreaterThanAssertion::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("gte"), GreaterThanEqualToAssertion::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("lt"), LessThanAssertion::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("lte"), LessThanOrEqualToAssertion::parse),
            new NamedXContentRegistry.Entry(ExecutableSection.class, new ParseField("length"), LengthAssertion::parse)));

    static ExecutableSection parse(XContentParser parser) throws IOException {
        ParserUtils.advanceToFieldName(parser);
        String section = parser.currentName();
        XContentLocation location = parser.getTokenLocation();
        try {
            ExecutableSection executableSection = parser.namedObject(ExecutableSection.class, section, null);
            parser.nextToken();
            return executableSection;
        } catch (Exception e) {
            throw new IOException("Error parsing section starting at [" + location + "]", e);
        }
    }

    /**
     * Get the location in the test that this was defined. 
     */
    XContentLocation getLocation();

    /**
     * Executes the section passing in the execution context
     */
    void execute(ClientYamlTestExecutionContext executionContext) throws IOException;
}
