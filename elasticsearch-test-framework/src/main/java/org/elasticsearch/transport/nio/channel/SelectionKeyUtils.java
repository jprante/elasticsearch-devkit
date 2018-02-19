package org.elasticsearch.transport.nio.channel;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;

public final class SelectionKeyUtils {

    private SelectionKeyUtils() {}

    public static void setWriteInterested(NioChannel channel) throws CancelledKeyException {
        SelectionKey selectionKey = channel.getSelectionKey();
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
    }

    public static void removeWriteInterested(NioChannel channel) throws CancelledKeyException {
        SelectionKey selectionKey = channel.getSelectionKey();
        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
    }

    public static void setConnectAndReadInterested(NioChannel channel) throws CancelledKeyException {
        SelectionKey selectionKey = channel.getSelectionKey();
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    }

    public static void removeConnectInterested(NioChannel channel) throws CancelledKeyException {
        SelectionKey selectionKey = channel.getSelectionKey();
        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_CONNECT);
    }

    public static void setAcceptInterested(NioServerSocketChannel channel) {
        SelectionKey selectionKey = channel.getSelectionKey();
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_ACCEPT);
    }
}
