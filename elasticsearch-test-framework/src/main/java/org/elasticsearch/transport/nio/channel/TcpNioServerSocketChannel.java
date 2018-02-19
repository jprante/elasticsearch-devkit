package org.elasticsearch.transport.nio.channel;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.transport.TcpChannel;
import org.elasticsearch.transport.nio.AcceptingSelector;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

/**
 * This is an implementation of {@link NioServerSocketChannel} that adheres to the {@link TcpChannel}
 * interface. As it is a server socket, setting SO_LINGER and sending messages is not supported.
 */
public class TcpNioServerSocketChannel extends NioServerSocketChannel implements TcpChannel {

    TcpNioServerSocketChannel(ServerSocketChannel socketChannel, TcpChannelFactory channelFactory, AcceptingSelector selector)
        throws IOException {
        super(socketChannel, channelFactory, selector);
    }

    @Override
    public void sendMessage(BytesReference reference, ActionListener<Void> listener) {
        throw new UnsupportedOperationException("Cannot send a message to a server channel.");
    }

    @Override
    public void setSoLinger(int value) throws IOException {
        throw new UnsupportedOperationException("Cannot set SO_LINGER on a server channel.");
    }

    @Override
    public String toString() {
        return "TcpNioServerSocketChannel{" +
            "localAddress=" + getLocalAddress() +
            '}';
    }
}
