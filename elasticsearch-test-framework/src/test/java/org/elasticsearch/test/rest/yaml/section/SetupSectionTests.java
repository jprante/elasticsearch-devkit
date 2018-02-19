package org.elasticsearch.test.rest.yaml.section;

import org.elasticsearch.Version;
import org.elasticsearch.common.xcontent.yaml.YamlXContent;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class SetupSectionTests extends AbstractClientYamlTestFragmentParserTestCase {
    public void testParseSetupSection() throws Exception {
        parser = createParser(YamlXContent.yamlXContent,
                "  - do:\n" +
                "      index1:\n" +
                "        index:  test_1\n" +
                "        type:   test\n" +
                "        id:     1\n" +
                "        body:   { \"include\": { \"field1\": \"v1\", \"field2\": \"v2\" }, \"count\": 1 }\n" +
                "  - do:\n" +
                "      index2:\n" +
                "        index:  test_1\n" +
                "        type:   test\n" +
                "        id:     2\n" +
                "        body:   { \"include\": { \"field1\": \"v1\", \"field2\": \"v2\" }, \"count\": 1 }\n"
        );

        SetupSection setupSection = SetupSection.parse(parser);

        assertThat(setupSection, notNullValue());
        assertThat(setupSection.getSkipSection().isEmpty(), equalTo(true));
        assertThat(setupSection.getDoSections().size(), equalTo(2));
        assertThat(setupSection.getDoSections().get(0).getApiCallSection().getApi(), equalTo("index1"));
        assertThat(setupSection.getDoSections().get(1).getApiCallSection().getApi(), equalTo("index2"));
    }

    public void testParseSetupAndSkipSectionNoSkip() throws Exception {
        parser = createParser(YamlXContent.yamlXContent,
                "  - skip:\n" +
                        "      version:  \"5.0.0 - 5.3.0\"\n" +
                        "      reason:   \"Update doesn't return metadata fields, waiting for #3259\"\n" +
                        "  - do:\n" +
                        "      index1:\n" +
                        "        index:  test_1\n" +
                        "        type:   test\n" +
                        "        id:     1\n" +
                        "        body:   { \"include\": { \"field1\": \"v1\", \"field2\": \"v2\" }, \"count\": 1 }\n" +
                        "  - do:\n" +
                        "      index2:\n" +
                        "        index:  test_1\n" +
                        "        type:   test\n" +
                        "        id:     2\n" +
                        "        body:   { \"include\": { \"field1\": \"v1\", \"field2\": \"v2\" }, \"count\": 1 }\n"
        );

        SetupSection setupSection = SetupSection.parse(parser);

        assertThat(setupSection, notNullValue());
        assertThat(setupSection.getSkipSection().isEmpty(), equalTo(false));
        assertThat(setupSection.getSkipSection(), notNullValue());
        assertThat(setupSection.getSkipSection().getLowerVersion(), equalTo(Version.V_5_0_0));
        assertThat(setupSection.getSkipSection().getUpperVersion(),
                equalTo(Version.V_5_3_0));
        assertThat(setupSection.getSkipSection().getReason(), equalTo("Update doesn't return metadata fields, waiting for #3259"));
        assertThat(setupSection.getDoSections().size(), equalTo(2));
        assertThat(setupSection.getDoSections().get(0).getApiCallSection().getApi(), equalTo("index1"));
        assertThat(setupSection.getDoSections().get(1).getApiCallSection().getApi(), equalTo("index2"));
    }
}
