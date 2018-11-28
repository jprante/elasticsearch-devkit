package org.elasticsearch.testframework.test.transport.nio.channel;

import org.elasticsearch.testframework.transport.nio.AcceptingSelector;
import org.elasticsearch.testframework.transport.nio.channel.ChannelFactory;
import org.elasticsearch.testframework.transport.nio.channel.NioServerSocketChannel;
import org.elasticsearch.testframework.test.transport.nio.utils.TestSelectionKey;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class DoNotRegisterServerChannel extends NioServerSocketChannel {

    public DoNotRegisterServerChannel(ServerSocketChannel channel, ChannelFactory channelFactory, AcceptingSelector selector)
        throws IOException {
        super(channel, channelFactory, selector);
    }

    @Override
    public void register() {
        setSelectionKey(new TestSelectionKey(0));
    }
}
