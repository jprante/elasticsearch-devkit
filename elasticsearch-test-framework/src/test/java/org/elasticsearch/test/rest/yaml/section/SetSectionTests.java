package org.elasticsearch.test.rest.yaml.section;

import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.yaml.YamlXContent;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class SetSectionTests extends AbstractClientYamlTestFragmentParserTestCase {
    public void testParseSetSectionSingleValue() throws Exception {
        parser = createParser(YamlXContent.yamlXContent,
                        "{ _id: id }"
        );

        SetSection setSection = SetSection.parse(parser);
        assertThat(setSection, notNullValue());
        assertThat(setSection.getStash(), notNullValue());
        assertThat(setSection.getStash().size(), equalTo(1));
        assertThat(setSection.getStash().get("_id"), equalTo("id"));
    }

    public void testParseSetSectionMultipleValues() throws Exception {
        parser = createParser(YamlXContent.yamlXContent,
                "{ _id: id, _type: type, _index: index }"
        );

        SetSection setSection = SetSection.parse(parser);
        assertThat(setSection, notNullValue());
        assertThat(setSection.getStash(), notNullValue());
        assertThat(setSection.getStash().size(), equalTo(3));
        assertThat(setSection.getStash().get("_id"), equalTo("id"));
        assertThat(setSection.getStash().get("_type"), equalTo("type"));
        assertThat(setSection.getStash().get("_index"), equalTo("index"));
    }

    public void testParseSetSectionNoValues() throws Exception {
        parser = createParser(YamlXContent.yamlXContent,
                "{ }"
        );

        Exception e = expectThrows(ParsingException.class, () -> SetSection.parse(parser));
        assertThat(e.getMessage(), is("set section must set at least a value"));
    }
}