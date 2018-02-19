package org.elasticsearch.transport.nio.channel;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.transport.nio.WriteOperation;

import java.io.IOException;

public interface WriteContext {

    void sendMessage(BytesReference reference, ActionListener<Void> listener);

    void queueWriteOperations(WriteOperation writeOperation);

    void flushChannel() throws IOException;

    boolean hasQueuedWriteOps();

    void clearQueuedWriteOps(Exception e);

}
