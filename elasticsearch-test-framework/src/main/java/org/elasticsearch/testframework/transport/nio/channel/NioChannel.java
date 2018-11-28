package org.elasticsearch.testframework.transport.nio.channel;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.testframework.transport.nio.ESSelector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectionKey;

public interface NioChannel {

    boolean isOpen();

    InetSocketAddress getLocalAddress();

    void close();

    void closeFromSelector() throws IOException;

    void register() throws ClosedChannelException;

    ESSelector getSelector();

    SelectionKey getSelectionKey();

    NetworkChannel getRawChannel();

    /**
     * Adds a close listener to the channel. Multiple close listeners can be added. There is no guarantee
     * about the order in which close listeners will be executed. If the channel is already closed, the
     * listener is executed immediately.
     *
     * @param listener to be called at close
     */
    void addCloseListener(ActionListener<Void> listener);
}
