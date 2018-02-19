package org.elasticsearch.index.mapper;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocValuesFieldExistsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryShardContext;

import java.io.IOException;
import java.util.List;

// this sucks how much must be overridden just do get a dummy field mapper...
public class MockFieldMapper extends FieldMapper {
    static Settings dummySettings = Settings.builder().put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT.id).build();

    public MockFieldMapper(String fullName) {
        this(fullName, new FakeFieldType());
    }

    public MockFieldMapper(String fullName, MappedFieldType fieldType) {
        super(findSimpleName(fullName), setName(fullName, fieldType), setName(fullName, fieldType), dummySettings,
            MultiFields.empty(), new CopyTo.Builder().build());
    }

    static MappedFieldType setName(String fullName, MappedFieldType fieldType) {
        fieldType.setName(fullName);
        return fieldType;
    }

    static String findSimpleName(String fullName) {
        int ndx = fullName.lastIndexOf('.');
        return fullName.substring(ndx + 1);
    }

    public static class FakeFieldType extends TermBasedFieldType {
        public FakeFieldType() {
        }

        protected FakeFieldType(FakeFieldType ref) {
            super(ref);
        }

        @Override
        public MappedFieldType clone() {
            return new FakeFieldType(this);
        }

        @Override
        public String typeName() {
            return "faketype";
        }

        @Override
        public Query existsQuery(QueryShardContext context) {
            if (hasDocValues()) {
                return new DocValuesFieldExistsQuery(name());
            } else {
                return new TermQuery(new Term(FieldNamesFieldMapper.NAME, name()));
            }
        }
    }

    @Override
    protected String contentType() {
        return null;
    }

    @Override
    protected void parseCreateField(ParseContext context, List list) throws IOException {
    }
}
