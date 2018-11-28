package org.elasticsearch.testframework.test.transport.nio;

import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.transport.nio.AcceptingSelector;
import org.elasticsearch.testframework.transport.nio.AcceptorEventHandler;
import org.elasticsearch.testframework.transport.nio.RoundRobinSupplier;
import org.elasticsearch.testframework.transport.nio.SocketSelector;
import org.elasticsearch.testframework.transport.nio.channel.ChannelFactory;
import org.elasticsearch.testframework.test.transport.nio.channel.DoNotRegisterServerChannel;
import org.elasticsearch.testframework.transport.nio.channel.NioServerSocketChannel;
import org.elasticsearch.testframework.transport.nio.channel.NioSocketChannel;
import org.elasticsearch.testframework.transport.nio.channel.ReadContext;
import org.elasticsearch.testframework.transport.nio.channel.WriteContext;
import org.junit.Before;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AcceptorEventHandlerTests extends ESTestCase {

    private AcceptorEventHandler handler;
    private SocketSelector socketSelector;
    private ChannelFactory channelFactory;
    private NioServerSocketChannel channel;
    private Consumer acceptedChannelCallback;

    @Before
    @SuppressWarnings("unchecked")
    public void setUpHandler() throws IOException {
        channelFactory = mock(ChannelFactory.class);
        socketSelector = mock(SocketSelector.class);
        acceptedChannelCallback = mock(Consumer.class);
        ArrayList<SocketSelector> selectors = new ArrayList<>();
        selectors.add(socketSelector);
        handler = new AcceptorEventHandler(logger, new RoundRobinSupplier<>(selectors.toArray(new SocketSelector[selectors.size()])));

        AcceptingSelector selector = mock(AcceptingSelector.class);
        channel = new DoNotRegisterServerChannel(mock(ServerSocketChannel.class), channelFactory, selector);
        channel.setAcceptContext(acceptedChannelCallback);
        channel.register();
    }

    public void testHandleRegisterSetsOP_ACCEPTInterest() {
        assertEquals(0, channel.getSelectionKey().interestOps());

        handler.serverChannelRegistered(channel);

        assertEquals(SelectionKey.OP_ACCEPT, channel.getSelectionKey().interestOps());
    }

    public void testHandleAcceptCallsChannelFactory() throws IOException {
        NioSocketChannel childChannel = new NioSocketChannel(mock(SocketChannel.class), socketSelector);
        when(channelFactory.acceptNioChannel(same(channel), same(socketSelector))).thenReturn(childChannel);

        handler.acceptChannel(channel);

        verify(channelFactory).acceptNioChannel(same(channel), same(socketSelector));

    }

    @SuppressWarnings("unchecked")
    public void testHandleAcceptCallsServerAcceptCallback() throws IOException {
        NioSocketChannel childChannel = new NioSocketChannel(mock(SocketChannel.class), socketSelector);
        childChannel.setContexts(mock(ReadContext.class), mock(WriteContext.class), mock(BiConsumer.class));
        when(channelFactory.acceptNioChannel(same(channel), same(socketSelector))).thenReturn(childChannel);

        handler.acceptChannel(channel);

        verify(acceptedChannelCallback).accept(childChannel);
    }
}
