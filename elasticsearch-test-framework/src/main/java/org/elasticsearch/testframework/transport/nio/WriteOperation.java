package org.elasticsearch.testframework.transport.nio;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.testframework.transport.nio.channel.NioSocketChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class WriteOperation {

    private final NioSocketChannel channel;
    private final ActionListener<Void> listener;
    private final ByteBuffer[] buffers;
    private final int[] offsets;
    private final int length;
    private int internalIndex;

    public WriteOperation(NioSocketChannel channel, BytesReference bytesReference, ActionListener<Void> listener) {
        this.channel = channel;
        this.listener = listener;
        this.buffers = toByteBuffers(bytesReference);
        this.offsets = new int[buffers.length];
        int offset = 0;
        for (int i = 0; i < buffers.length; i++) {
            ByteBuffer buffer = buffers[i];
            offsets[i] = offset;
            offset += buffer.remaining();
        }
        length = offset;
    }

    public ByteBuffer[] getByteBuffers() {
        return buffers;
    }

    public ActionListener<Void> getListener() {
        return listener;
    }

    public NioSocketChannel getChannel() {
        return channel;
    }

    public boolean isFullyFlushed() {
        return internalIndex == length;
    }

    public int flush() throws IOException {
        int written = channel.write(getBuffersToWrite());
        internalIndex += written;
        return written;
    }

    private ByteBuffer[] getBuffersToWrite() {
        int offsetIndex = getOffsetIndex(internalIndex);

        ByteBuffer[] postIndexBuffers = new ByteBuffer[buffers.length - offsetIndex];

        ByteBuffer firstBuffer = buffers[offsetIndex].duplicate();
        firstBuffer.position(internalIndex - offsets[offsetIndex]);
        postIndexBuffers[0] = firstBuffer;
        int j = 1;
        for (int i = (offsetIndex + 1); i < buffers.length; ++i) {
            postIndexBuffers[j++] = buffers[i].duplicate();
        }

        return postIndexBuffers;
    }

    private int getOffsetIndex(int offset) {
        final int i = Arrays.binarySearch(offsets, offset);
        return i < 0 ? (-(i + 1)) - 1 : i;
    }

    private static ByteBuffer[] toByteBuffers(BytesReference bytesReference) {
        BytesRefIterator byteRefIterator = bytesReference.iterator();
        BytesRef r;
        try {
            // Most network messages are composed of three buffers.
            ArrayList<ByteBuffer> buffers = new ArrayList<>(3);
            while ((r = byteRefIterator.next()) != null) {
                buffers.add(ByteBuffer.wrap(r.bytes, r.offset, r.length));
            }
            return buffers.toArray(new ByteBuffer[buffers.size()]);

        } catch (IOException e) {
            // this is really an error since we don't do IO in our bytesreferences
            throw new AssertionError("won't happen", e);
        }
    }
}
