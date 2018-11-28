package org.elasticsearch.testframework;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.cluster.AbstractNamedDiffable;
import org.elasticsearch.cluster.NamedDiff;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.function.Function;

public abstract class TestCustomMetaData extends AbstractNamedDiffable<MetaData.Custom> implements MetaData.Custom {
    private final String data;

    protected TestCustomMetaData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestCustomMetaData that = (TestCustomMetaData) o;

        if (!data.equals(that.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    protected static <T extends TestCustomMetaData> T readFrom(Function<String, T> supplier, StreamInput in) throws IOException {
        return supplier.apply(in.readString());
    }

    public static NamedDiff<MetaData.Custom> readDiffFrom(String name, StreamInput in)  throws IOException {
        return readDiffFrom(MetaData.Custom.class, name, in);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(getData());
    }

    @SuppressWarnings("unchecked")
    public static <T extends MetaData.Custom> T fromXContent(Function<String, MetaData.Custom> supplier, XContentParser parser)
        throws IOException {
        XContentParser.Token token;
        String data = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                String currentFieldName = parser.currentName();
                if ("data".equals(currentFieldName)) {
                    if (parser.nextToken() != XContentParser.Token.VALUE_STRING) {
                        throw new ElasticsearchParseException("failed to parse snapshottable metadata, invalid data type");
                    }
                    data = parser.text();
                } else {
                    throw new ElasticsearchParseException("failed to parse snapshottable metadata, unknown field [{}]", currentFieldName);
                }
            } else {
                throw new ElasticsearchParseException("failed to parse snapshottable metadata");
            }
        }
        if (data == null) {
            throw new ElasticsearchParseException("failed to parse snapshottable metadata, data not found");
        }
        return (T) supplier.apply(data);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.field("data", getData());
        return builder;
    }

    @Override
    public String toString() {
        return "[" + getWriteableName() + "][" + data +"]";
    }
}
