package org.elasticsearch.transport.nio.channel;

import org.elasticsearch.transport.nio.SocketSelector;
import org.elasticsearch.transport.nio.utils.TestSelectionKey;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class DoNotRegisterChannel extends NioSocketChannel {

    public DoNotRegisterChannel(SocketChannel socketChannel, SocketSelector selector) throws IOException {
        super(socketChannel, selector);
    }

    @Override
    public void register() {
        setSelectionKey(new TestSelectionKey(0));
    }
}
