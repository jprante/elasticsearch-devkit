package org.elasticsearch.analysis.common;

import com.carrotsearch.randomizedtesting.annotations.Name;
import com.carrotsearch.randomizedtesting.annotations.ParametersFactory;

import org.elasticsearch.test.rest.yaml.ClientYamlTestCandidate;
import org.elasticsearch.test.rest.yaml.ESClientYamlSuiteTestCase;

public class CommonAnalysisClientYamlTestSuiteIT extends ESClientYamlSuiteTestCase {
    public CommonAnalysisClientYamlTestSuiteIT(@Name("yaml")ClientYamlTestCandidate testCandidate) {
        super(testCandidate);
    }

    @ParametersFactory
    public static Iterable<Object[]> parameters() throws Exception {
        return ESClientYamlSuiteTestCase.createParameters(CommonAnalysisClientYamlTestSuiteIT.class.getClassLoader());
    }
}
