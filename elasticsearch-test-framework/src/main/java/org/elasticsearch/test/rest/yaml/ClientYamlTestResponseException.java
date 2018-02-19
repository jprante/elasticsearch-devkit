package org.elasticsearch.test.rest.yaml;

import org.elasticsearch.client.ResponseException;

import java.io.IOException;

/**
 * Exception obtained from a REST call in case the response code indicated an error.
 * Eagerly reads the response body into a string for later optional parsing.
 * Supports parsing the response body when needed and returning specific values extracted from it.
 */
public class ClientYamlTestResponseException extends IOException {

    private final ClientYamlTestResponse restTestResponse;
    private final ResponseException responseException;

    public ClientYamlTestResponseException(ResponseException responseException) throws IOException {
        super(responseException);
        this.responseException = responseException;
        this.restTestResponse = new ClientYamlTestResponse(responseException.getResponse());
    }

    /**
     * Exposes the obtained response body
     */
    public ClientYamlTestResponse getRestTestResponse() {
        return restTestResponse;
    }

    /**
     * Exposes the origina {@link ResponseException}. Note that the entity will always be null as it
     * gets eagerly consumed and exposed through {@link #getRestTestResponse()}.
     */
    public ResponseException getResponseException() {
        return responseException;
    }
}
