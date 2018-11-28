package org.elasticsearch.testframework.transport.nio.channel;

import org.elasticsearch.transport.TcpTransport;
import org.elasticsearch.testframework.transport.nio.AcceptingSelector;
import org.elasticsearch.testframework.transport.nio.SocketSelector;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

/**
 * This is an implementation of {@link ChannelFactory} which returns channels that adhere to the
 * {@link org.elasticsearch.transport.TcpChannel} interface. The channels will use the provided
 * {@link TcpTransport.ProfileSettings}. The provided context setters will be called with the channel after
 * construction.
 */
public class TcpChannelFactory extends ChannelFactory<TcpNioServerSocketChannel, TcpNioSocketChannel> {

    private final Consumer<NioSocketChannel> contextSetter;
    private final Consumer<NioServerSocketChannel> serverContextSetter;

    public TcpChannelFactory(TcpTransport.ProfileSettings profileSettings, Consumer<NioSocketChannel> contextSetter,
                             Consumer<NioServerSocketChannel> serverContextSetter) {
        super(new RawChannelFactory(profileSettings.tcpNoDelay,
            profileSettings.tcpKeepAlive,
            profileSettings.reuseAddress,
            Math.toIntExact(profileSettings.sendBufferSize.getBytes()),
            Math.toIntExact(profileSettings.receiveBufferSize.getBytes())));
        this.contextSetter = contextSetter;
        this.serverContextSetter = serverContextSetter;
    }

    @Override
    public TcpNioSocketChannel createChannel(SocketSelector selector, SocketChannel channel) throws IOException {
        TcpNioSocketChannel nioChannel = new TcpNioSocketChannel(channel, selector);
        contextSetter.accept(nioChannel);
        return nioChannel;
    }

    @Override
    public TcpNioServerSocketChannel createServerChannel(AcceptingSelector selector, ServerSocketChannel channel) throws IOException {
        TcpNioServerSocketChannel nioServerChannel = new TcpNioServerSocketChannel(channel, this, selector);
        serverContextSetter.accept(nioServerChannel);
        return nioServerChannel;
    }
}
