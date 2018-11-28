package org.elasticsearch.testframework.transport.nio.channel;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.testframework.transport.nio.SocketSelector;
import org.elasticsearch.testframework.transport.nio.WriteOperation;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.LinkedList;

public class TcpWriteContext implements WriteContext {

    private final NioSocketChannel channel;
    private final LinkedList<WriteOperation> queued = new LinkedList<>();

    public TcpWriteContext(NioSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(BytesReference reference, ActionListener<Void> listener) {
        if (!channel.isWritable()) {
            listener.onFailure(new ClosedChannelException());
            return;
        }

        WriteOperation writeOperation = new WriteOperation(channel, reference, listener);
        SocketSelector selector = channel.getSelector();
        if (!selector.isOnCurrentThread()) {
            selector.queueWrite(writeOperation);
            return;
        }

        // TODO: Eval if we will allow writes from sendMessage
        selector.queueWriteInChannelBuffer(writeOperation);
    }

    @Override
    public void queueWriteOperations(WriteOperation writeOperation) {
        assert channel.getSelector().isOnCurrentThread() : "Must be on selector thread to queue writes";
        queued.add(writeOperation);
    }

    @Override
    public void flushChannel() throws IOException {
        assert channel.getSelector().isOnCurrentThread() : "Must be on selector thread to flush writes";
        int ops = queued.size();
        if (ops == 1) {
            singleFlush(queued.pop());
        } else if (ops > 1) {
            multiFlush();
        }
    }

    @Override
    public boolean hasQueuedWriteOps() {
        assert channel.getSelector().isOnCurrentThread() : "Must be on selector thread to access queued writes";
        return queued.isEmpty() == false;
    }

    @Override
    public void clearQueuedWriteOps(Exception e) {
        assert channel.getSelector().isOnCurrentThread() : "Must be on selector thread to clear queued writes";
        for (WriteOperation op : queued) {
            channel.getSelector().executeFailedListener(op.getListener(), e);
        }
        queued.clear();
    }

    private void singleFlush(WriteOperation headOp) throws IOException {
        try {
            headOp.flush();
        } catch (IOException e) {
            channel.getSelector().executeFailedListener(headOp.getListener(), e);
            throw e;
        }

        if (headOp.isFullyFlushed()) {
            channel.getSelector().executeListener(headOp.getListener(), null);
        } else {
            queued.push(headOp);
        }
    }

    private void multiFlush() throws IOException {
        boolean lastOpCompleted = true;
        while (lastOpCompleted && queued.isEmpty() == false) {
            WriteOperation op = queued.pop();
            singleFlush(op);
            lastOpCompleted = op.isFullyFlushed();
        }
    }
}
