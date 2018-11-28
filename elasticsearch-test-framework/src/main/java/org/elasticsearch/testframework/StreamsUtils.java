package org.elasticsearch.testframework;

import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.io.stream.BytesStreamOutput;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StreamsUtils {

    public static String copyToStringFromClasspath(Class clazz, String path) throws IOException {
        InputStream is = clazz.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Resource [" + path + "] not found in classpath with class [" + clazz.getName() + "]");
        }
        return Streams.copyToString(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    public static byte[] copyToBytesFromClasspath(Class clazz, String path) throws IOException {
        try (InputStream is = clazz.getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Resource [" + path + "] not found in classpath");
            }
            try (BytesStreamOutput out = new BytesStreamOutput()) {
                Streams.copy(is, out);
                return BytesReference.toBytes(out.bytes());
            }
        }
    }

}
