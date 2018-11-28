package org.elasticsearch.testframework.transport.nio;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.elasticsearch.testframework.transport.nio.channel.ChannelFactory;
import org.elasticsearch.testframework.transport.nio.channel.NioServerSocketChannel;
import org.elasticsearch.testframework.transport.nio.channel.NioSocketChannel;
import org.elasticsearch.testframework.transport.nio.channel.SelectionKeyUtils;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Event handler designed to handle events from server sockets
 */
public class AcceptorEventHandler extends EventHandler {

    private final Supplier<SocketSelector> selectorSupplier;

    public AcceptorEventHandler(Logger logger, Supplier<SocketSelector> selectorSupplier) {
        super(logger);
        this.selectorSupplier = selectorSupplier;
    }

    /**
     * This method is called when a NioServerSocketChannel is successfully registered. It should only be
     * called once per channel.
     *
     * @param nioServerSocketChannel that was registered
     */
    public void serverChannelRegistered(NioServerSocketChannel nioServerSocketChannel) {
        SelectionKeyUtils.setAcceptInterested(nioServerSocketChannel);
    }

    /**
     * This method is called when an attempt to register a server channel throws an exception.
     *
     * @param channel that was registered
     * @param exception that occurred
     */
    public void registrationException(NioServerSocketChannel channel, Exception exception) {
        logger.error(new ParameterizedMessage("failed to register server channel: {}", channel), exception);
    }

    /**
     * This method is called when a server channel signals it is ready to accept a connection. All of the
     * accept logic should occur in this call.
     *
     * @param nioServerChannel that can accept a connection
     */
    public void acceptChannel(NioServerSocketChannel nioServerChannel) throws IOException {
        ChannelFactory channelFactory = nioServerChannel.getChannelFactory();
        SocketSelector selector = selectorSupplier.get();
        NioSocketChannel nioSocketChannel = channelFactory.acceptNioChannel(nioServerChannel, selector);
        nioServerChannel.getAcceptContext().accept(nioSocketChannel);
    }

    /**
     * This method is called when an attempt to accept a connection throws an exception.
     *
     * @param nioServerChannel that accepting a connection
     * @param exception that occurred
     */
    public void acceptException(NioServerSocketChannel nioServerChannel, Exception exception) {
        logger.debug(() -> new ParameterizedMessage("exception while accepting new channel from server channel: {}",
            nioServerChannel), exception);
    }
}
