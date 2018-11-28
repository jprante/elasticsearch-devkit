package org.elasticsearch.testframework.transport.nio.channel;

import org.elasticsearch.testframework.transport.nio.AcceptingSelector;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.function.Consumer;

public class NioServerSocketChannel extends AbstractNioChannel<ServerSocketChannel> {

    private final ChannelFactory channelFactory;
    private Consumer<NioSocketChannel> acceptContext;

    public NioServerSocketChannel(ServerSocketChannel socketChannel, ChannelFactory channelFactory, AcceptingSelector selector)
        throws IOException {
        super(socketChannel, selector);
        this.channelFactory = channelFactory;
    }

    public ChannelFactory getChannelFactory() {
        return channelFactory;
    }

    /**
     * This method sets the accept context for a server socket channel. The accept context is called when a
     * new channel is accepted. The parameter passed to the context is the new channel.
     *
     * @param acceptContext to call
     */
    public void setAcceptContext(Consumer<NioSocketChannel> acceptContext) {
        this.acceptContext = acceptContext;
    }

    public Consumer<NioSocketChannel> getAcceptContext() {
        return acceptContext;
    }

    @Override
    public String toString() {
        return "NioServerSocketChannel{" +
            "localAddress=" + getLocalAddress() +
            '}';
    }
}
