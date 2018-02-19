package org.elasticsearch.transport.nio.channel;

import java.io.IOException;

public interface ReadContext extends AutoCloseable {

    int read() throws IOException;

    @Override
    void close();

}
