package org.elasticsearch.testframework.test.transport.nio.channel;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.transport.nio.SocketEventHandler;
import org.elasticsearch.testframework.transport.nio.SocketSelector;
import org.elasticsearch.testframework.transport.nio.channel.NioSocketChannel;
import org.elasticsearch.testframework.transport.nio.channel.ReadContext;
import org.elasticsearch.testframework.transport.nio.channel.WriteContext;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NioSocketChannelTests extends ESTestCase {

    private SocketSelector selector;
    private AtomicBoolean closedRawChannel;
    private Thread thread;

    @Before
    @SuppressWarnings("unchecked")
    public void startSelector() throws IOException {
        selector = new SocketSelector(new SocketEventHandler(logger));
        thread = new Thread(selector::runLoop);
        closedRawChannel = new AtomicBoolean(false);
        thread.start();
        selector.isRunningFuture().actionGet();
    }

    @After
    public void stopSelector() throws IOException, InterruptedException {
        selector.close();
        thread.join();
    }

    @SuppressWarnings("unchecked")
    public void testClose() throws Exception {
        AtomicBoolean isClosed = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        NioSocketChannel socketChannel = new DoNotCloseChannel(mock(SocketChannel.class), selector);
        socketChannel.setContexts(mock(ReadContext.class), mock(WriteContext.class), mock(BiConsumer.class));
        socketChannel.addCloseListener(new ActionListener<Void>() {
            @Override
            public void onResponse(Void o) {
                isClosed.set(true);
                latch.countDown();
            }
            @Override
            public void onFailure(Exception e) {
                isClosed.set(true);
                latch.countDown();
            }
        });

        assertTrue(socketChannel.isOpen());
        assertFalse(closedRawChannel.get());
        assertFalse(isClosed.get());

        PlainActionFuture<Void> closeFuture = PlainActionFuture.newFuture();
        socketChannel.addCloseListener(closeFuture);
        socketChannel.close();
        closeFuture.actionGet();

        assertTrue(closedRawChannel.get());
        assertFalse(socketChannel.isOpen());
        latch.await();
        assertTrue(isClosed.get());
    }

    @SuppressWarnings("unchecked")
    public void testConnectSucceeds() throws Exception {
        SocketChannel rawChannel = mock(SocketChannel.class);
        when(rawChannel.finishConnect()).thenReturn(true);
        NioSocketChannel socketChannel = new DoNotCloseChannel(rawChannel, selector);
        socketChannel.setContexts(mock(ReadContext.class), mock(WriteContext.class), mock(BiConsumer.class));
        selector.scheduleForRegistration(socketChannel);

        PlainActionFuture<Void> connectFuture = PlainActionFuture.newFuture();
        socketChannel.addConnectListener(connectFuture);
        connectFuture.get(100, TimeUnit.SECONDS);

        assertTrue(socketChannel.isConnectComplete());
        assertTrue(socketChannel.isOpen());
        assertFalse(closedRawChannel.get());
    }

    @SuppressWarnings("unchecked")
    public void testConnectFails() throws Exception {
        SocketChannel rawChannel = mock(SocketChannel.class);
        when(rawChannel.finishConnect()).thenThrow(new ConnectException());
        NioSocketChannel socketChannel = new DoNotCloseChannel(rawChannel, selector);
        socketChannel.setContexts(mock(ReadContext.class), mock(WriteContext.class), mock(BiConsumer.class));
        selector.scheduleForRegistration(socketChannel);

        PlainActionFuture<Void> connectFuture = PlainActionFuture.newFuture();
        socketChannel.addConnectListener(connectFuture);
        ExecutionException e = expectThrows(ExecutionException.class, () -> connectFuture.get(100, TimeUnit.SECONDS));
        assertTrue(e.getCause() instanceof IOException);

        assertFalse(socketChannel.isConnectComplete());
        // Even if connection fails the channel is 'open' until close() is called
        assertTrue(socketChannel.isOpen());
    }

    private class DoNotCloseChannel extends DoNotRegisterChannel {

        private DoNotCloseChannel(SocketChannel channel, SocketSelector selector) throws IOException {
            super(channel, selector);
        }

        @Override
        public void closeRawChannel() throws IOException {
            closedRawChannel.set(true);
        }
    }
}
