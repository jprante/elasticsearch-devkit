package org.elasticsearch.analysis.common.test;

import com.carrotsearch.randomizedtesting.annotations.Name;
import com.carrotsearch.randomizedtesting.annotations.ParametersFactory;

import org.elasticsearch.testframework.rest.yaml.ClientYamlTestCandidate;
import org.elasticsearch.testframework.rest.yaml.ESClientYamlSuiteTestCase;

public class CommonAnalysisClientYamlTestSuiteIT extends ESClientYamlSuiteTestCase {

    public CommonAnalysisClientYamlTestSuiteIT(@Name("yaml")ClientYamlTestCandidate testCandidate) {
        super(testCandidate);
    }

    @ParametersFactory
    public static Iterable<Object[]> parameters() throws Exception {
        return ESClientYamlSuiteTestCase.createParameters(CommonAnalysisClientYamlTestSuiteIT.class.getClassLoader());
    }
}
