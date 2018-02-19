package org.elasticsearch.client;

import java.util.HashMap;
import java.util.Map;

import joptsimple.internal.Strings;
import org.apache.http.Header;
import org.elasticsearch.test.ESTestCase;

/**
 * A test case with access to internals of a RestClient.
 */
public abstract class RestClientBuilderTestCase extends ESTestCase {
    /** Checks the given rest client has the provided default headers. */
    public void assertHeaders(RestClient client, Map<String, String> expectedHeaders) {
        expectedHeaders = new HashMap<>(expectedHeaders); // copy so we can remove as we check
        for (Header header : client.defaultHeaders) {
            String name = header.getName();
            String expectedValue = expectedHeaders.remove(name);
            if (expectedValue == null) {
                fail("Found unexpected header in rest client: " + name);
            }
            assertEquals(expectedValue, header.getValue());
        }
        if (!expectedHeaders.isEmpty()) {
            fail("Missing expected headers in rest client: " + Strings.join(expectedHeaders.keySet(), ", "));
        }
    }
}
