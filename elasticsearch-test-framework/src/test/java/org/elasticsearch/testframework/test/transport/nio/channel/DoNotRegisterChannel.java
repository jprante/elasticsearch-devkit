package org.elasticsearch.testframework.test.transport.nio.channel;

import org.elasticsearch.testframework.transport.nio.SocketSelector;
import org.elasticsearch.testframework.transport.nio.channel.NioSocketChannel;
import org.elasticsearch.testframework.test.transport.nio.utils.TestSelectionKey;

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
