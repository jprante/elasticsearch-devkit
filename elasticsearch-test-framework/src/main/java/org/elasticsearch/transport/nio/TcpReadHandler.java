package org.elasticsearch.transport.nio;

import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.transport.nio.channel.NioSocketChannel;
import org.elasticsearch.transport.nio.channel.TcpNioSocketChannel;

import java.io.IOException;

public class TcpReadHandler {

    private final String profile;
    private final NioTransport transport;

    public TcpReadHandler(String profile, NioTransport transport) {
        this.profile = profile;
        this.transport = transport;
    }

    public void handleMessage(BytesReference reference, TcpNioSocketChannel channel, int messageBytesLength) {
        try {
            transport.messageReceived(reference, channel, profile, channel.getRemoteAddress(), messageBytesLength);
        } catch (IOException e) {
            handleException(channel, e);
        }
    }

    public void handleException(NioSocketChannel channel, Exception e) {
        transport.exceptionCaught(channel, e);
    }
}
