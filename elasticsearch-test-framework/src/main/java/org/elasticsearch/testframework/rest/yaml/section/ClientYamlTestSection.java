package org.elasticsearch.testframework.rest.yaml.section;

import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a test section, which is composed of a skip section and multiple executable sections.
 */
public class ClientYamlTestSection implements Comparable<ClientYamlTestSection> {
    public static ClientYamlTestSection parse(XContentParser parser) throws IOException {
        ParserUtils.advanceToFieldName(parser);
        ClientYamlTestSection testSection = new ClientYamlTestSection(parser.getTokenLocation(), parser.currentName());
        try {
            parser.nextToken();
            testSection.setSkipSection(SkipSection.parseIfNext(parser));
            while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                ParserUtils.advanceToFieldName(parser);
                testSection.addExecutableSection(ExecutableSection.parse(parser));
            }
            if (parser.nextToken() != XContentParser.Token.END_OBJECT) {
                throw new IllegalArgumentException("malformed section [" + testSection.getName() + "] expected ["
                        + XContentParser.Token.END_OBJECT + "] but was [" + parser.currentToken() + "]");
            }
            parser.nextToken();
            return testSection;
        } catch (Exception e) {
            throw new ParsingException(parser.getTokenLocation(), "Error parsing test named [" + testSection.getName() + "]", e);
        }
    }

    private final XContentLocation location;
    private final String name;
    private SkipSection skipSection;
    private final List<ExecutableSection> executableSections;

    public ClientYamlTestSection(XContentLocation location, String name) {
        this.location = location;
        this.name = name;
        this.executableSections = new ArrayList<>();
    }

    public XContentLocation getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public SkipSection getSkipSection() {
        return skipSection;
    }

    public void setSkipSection(SkipSection skipSection) {
        this.skipSection = skipSection;
    }

    public List<ExecutableSection> getExecutableSections() {
        return executableSections;
    }

    public void addExecutableSection(ExecutableSection executableSection) {
        if (executableSection instanceof DoSection) {
            DoSection doSection = (DoSection) executableSection;
            if (!doSection.getExpectedWarningHeaders().isEmpty()
                    && !skipSection.getFeatures().contains("warnings")) {
                throw new IllegalArgumentException("Attempted to add a [do] with a [warnings] section without a corresponding [skip] so "
                        + "runners that do not support the [warnings] section can skip the test at line ["
                        + doSection.getLocation().lineNumber + "]");
            }
        }
        this.executableSections.add(executableSection);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientYamlTestSection that = (ClientYamlTestSection) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public int compareTo(ClientYamlTestSection o) {
        return name.compareTo(o.getName());
    }
}
