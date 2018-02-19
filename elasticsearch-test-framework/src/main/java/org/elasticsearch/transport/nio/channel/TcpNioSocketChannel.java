package org.elasticsearch.transport.nio.channel;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.transport.TcpChannel;
import org.elasticsearch.transport.nio.SocketSelector;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

public class TcpNioSocketChannel extends NioSocketChannel implements TcpChannel {

    public TcpNioSocketChannel(SocketChannel socketChannel, SocketSelector selector) throws IOException {
        super(socketChannel, selector);
    }

    public void sendMessage(BytesReference reference, ActionListener<Void> listener) {
        getWriteContext().sendMessage(reference, listener);
    }

    @Override
    public void setSoLinger(int value) throws IOException {
        if (isOpen()) {
            getRawChannel().setOption(StandardSocketOptions.SO_LINGER, value);
        }
    }

    @Override
    public String toString() {
        return "TcpNioSocketChannel{" +
            "localAddress=" + getLocalAddress() +
            ", remoteAddress=" + getRemoteAddress() +
            '}';
    }
}
