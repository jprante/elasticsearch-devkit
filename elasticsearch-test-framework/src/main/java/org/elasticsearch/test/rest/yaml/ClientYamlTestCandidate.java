package org.elasticsearch.test.rest.yaml;

import org.elasticsearch.test.rest.yaml.section.ClientYamlTestSuite;
import org.elasticsearch.test.rest.yaml.section.SetupSection;
import org.elasticsearch.test.rest.yaml.section.TeardownSection;
import org.elasticsearch.test.rest.yaml.section.ClientYamlTestSection;

/**
 * Wraps {@link ClientYamlTestSection}s ready to be run. Each test section is associated to its {@link ClientYamlTestSuite}.
 */
public class ClientYamlTestCandidate {

    private final ClientYamlTestSuite restTestSuite;
    private final ClientYamlTestSection testSection;

    public ClientYamlTestCandidate(ClientYamlTestSuite restTestSuite, ClientYamlTestSection testSection) {
        this.restTestSuite = restTestSuite;
        this.testSection = testSection;
    }

    public String getApi() {
        return restTestSuite.getApi();
    }

    public String getName() {
        return restTestSuite.getName();
    }

    public String getSuitePath() {
        return restTestSuite.getPath();
    }

    public String getTestPath() {
        return restTestSuite.getPath() + "/" + testSection.getName();
    }

    public SetupSection getSetupSection() {
        return restTestSuite.getSetupSection();
    }

    public TeardownSection getTeardownSection() {
        return restTestSuite.getTeardownSection();
    }

    public ClientYamlTestSection getTestSection() {
        return testSection;
    }

    @Override
    public String toString() {
        return getTestPath();
    }
}
