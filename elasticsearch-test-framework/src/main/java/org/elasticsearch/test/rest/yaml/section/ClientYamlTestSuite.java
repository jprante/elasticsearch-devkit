package org.elasticsearch.test.rest.yaml.section;

import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.yaml.YamlXContent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Holds a REST test suite loaded from a specific yaml resource.
 * Supports a setup section and multiple test sections.
 */
public class ClientYamlTestSuite {

    public static ClientYamlTestSuite parse(String api, URL url) throws IOException {

        String filename = url.getPath();
        // cut last path component
        int i = filename.lastIndexOf('/');
        if (i > 0) {
            filename = filename.substring(i + 1);
        }
        // remove the file extension
        i = filename.lastIndexOf('.');
        if (i > 0) {
            filename = filename.substring(0, i);
        }
        try (XContentParser parser = YamlXContent.yamlXContent
                .createParser(ExecutableSection.XCONTENT_REGISTRY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, url.openStream())) {
            return parse(api, filename, parser);
        } catch(Exception e) {
            throw new IOException("Error parsing " + api + "/" + filename, e);
        }
    }

    public static ClientYamlTestSuite parse(String api, String suiteName, XContentParser parser) throws IOException {
        parser.nextToken();
        assert parser.currentToken() == XContentParser.Token.START_OBJECT : "expected token to be START_OBJECT but was "
                + parser.currentToken();

        ClientYamlTestSuite restTestSuite = new ClientYamlTestSuite(api, suiteName);

        restTestSuite.setSetupSection(SetupSection.parseIfNext(parser));
        restTestSuite.setTeardownSection(TeardownSection.parseIfNext(parser));

        while(true) {
            //the "---" section separator is not understood by the yaml parser. null is returned, same as when the parser is closed
            //we need to somehow distinguish between a null in the middle of a test ("---")
            // and a null at the end of the file (at least two consecutive null tokens)
            if(parser.currentToken() == null) {
                if (parser.nextToken() == null) {
                    break;
                }
            }

            ClientYamlTestSection testSection = ClientYamlTestSection.parse(parser);
            if (!restTestSuite.addTestSection(testSection)) {
                throw new ParsingException(testSection.getLocation(), "duplicate test section [" + testSection.getName() + "]");
            }
        }

        return restTestSuite;
    }

    private final String api;
    private final String name;

    private SetupSection setupSection;
    private TeardownSection teardownSection;

    private Set<ClientYamlTestSection> testSections = new TreeSet<>();

    public ClientYamlTestSuite(String api, String name) {
        this.api = api;
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return api + "/" + name;
    }

    public SetupSection getSetupSection() {
        return setupSection;
    }

    public void setSetupSection(SetupSection setupSection) {
        this.setupSection = setupSection;
    }

    public TeardownSection getTeardownSection() {
        return teardownSection;
    }

    public void setTeardownSection(TeardownSection teardownSection) {
        this.teardownSection = teardownSection;
    }

    /**
     * Adds a {@link org.elasticsearch.test.rest.yaml.section.ClientYamlTestSection} to the REST suite
     * @return true if the test section was not already present, false otherwise
     */
    public boolean addTestSection(ClientYamlTestSection testSection) {
        return this.testSections.add(testSection);
    }

    public List<ClientYamlTestSection> getTestSections() {
        return new ArrayList<>(testSections);
    }
}
