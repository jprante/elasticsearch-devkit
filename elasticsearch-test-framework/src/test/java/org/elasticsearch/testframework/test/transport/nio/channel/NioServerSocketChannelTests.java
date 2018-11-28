package org.elasticsearch.testframework.test.transport.nio.channel;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.transport.nio.AcceptingSelector;
import org.elasticsearch.testframework.transport.nio.AcceptorEventHandler;
import org.elasticsearch.testframework.transport.nio.channel.ChannelFactory;
import org.elasticsearch.testframework.transport.nio.channel.NioChannel;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;

public class NioServerSocketChannelTests extends ESTestCase {

    private AcceptingSelector selector;
    private AtomicBoolean closedRawChannel;
    private Thread thread;

    @Before
    @SuppressWarnings("unchecked")
    public void setSelector() throws IOException {
        selector = new AcceptingSelector(new AcceptorEventHandler(logger, mock(Supplier.class)));
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

    public void testClose() throws Exception {
        AtomicBoolean isClosed = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        NioChannel channel = new DoNotCloseServerChannel("nio", mock(ServerSocketChannel.class), mock(ChannelFactory.class), selector);

        channel.addCloseListener(new ActionListener<Void>() {
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

        assertTrue(channel.isOpen());
        assertFalse(closedRawChannel.get());
        assertFalse(isClosed.get());

        PlainActionFuture<Void> closeFuture = PlainActionFuture.newFuture();
        channel.addCloseListener(closeFuture);
        channel.close();
        closeFuture.actionGet();


        assertTrue(closedRawChannel.get());
        assertFalse(channel.isOpen());
        latch.await();
        assertTrue(isClosed.get());
    }

    private class DoNotCloseServerChannel extends DoNotRegisterServerChannel {

        private DoNotCloseServerChannel(String profile, ServerSocketChannel channel, ChannelFactory channelFactory,
                                        AcceptingSelector selector) throws IOException {
            super(channel, channelFactory, selector);
        }

        @Override
        public void closeRawChannel() {
            closedRawChannel.set(true);
        }
    }
}
