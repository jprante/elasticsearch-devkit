package org.elasticsearch.testframework.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.elasticsearch.client.rest.RestClient;
import org.elasticsearch.testframework.ESTestCase;

/**
 * A test case with access to internals of a RestClient.
 */
public abstract class RestClientBuilderTestCase extends ESTestCase {
    /** Checks the given rest client has the provided default headers. */
    public void assertHeaders(RestClient client, Map<String, String> expectedHeaders) {
        expectedHeaders = new HashMap<>(expectedHeaders); // copy so we can remove as we check
        for (Header header : client.getDefaultHeaders()) {
            String name = header.getName();
            String expectedValue = expectedHeaders.remove(name);
            if (expectedValue == null) {
                fail("Found unexpected header in rest client: " + name);
            }
            assertEquals(expectedValue, header.getValue());
        }
        if (!expectedHeaders.isEmpty()) {
            fail("Missing expected headers in rest client: " + String.join(", ",expectedHeaders.keySet()));
        }
    }
}
