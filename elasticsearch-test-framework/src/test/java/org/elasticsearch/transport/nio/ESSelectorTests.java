package org.elasticsearch.transport.nio;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.transport.nio.channel.NioChannel;
import org.junit.Before;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ESSelectorTests extends ESTestCase {

    private ESSelector selector;
    private EventHandler handler;
    private Selector rawSelector;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        handler = mock(EventHandler.class);
        rawSelector = mock(Selector.class);
        selector = new TestSelector(handler, rawSelector);
    }

    public void testQueueChannelForClosed() throws IOException {
        NioChannel channel = mock(NioChannel.class);
        when(channel.getSelector()).thenReturn(selector);

        selector.queueChannelClose(channel);

        selector.singleLoop();

        verify(handler).handleClose(channel);
    }

    @AwaitsFix(bugUrl = "fails with updated securemock - assertTrue fails")
    public void testSelectorClosedExceptionIsNotCaughtWhileRunning() throws IOException {
        boolean closedSelectorExceptionCaught = false;
        when(rawSelector.select(anyInt())).thenThrow(new ClosedSelectorException());
        try {
            this.selector.singleLoop();
        } catch (ClosedSelectorException e) {
            closedSelectorExceptionCaught = true;
        }

        assertTrue(closedSelectorExceptionCaught);
    }

    @AwaitsFix(bugUrl = "fails with updated securemock, IOException never thrown")
    public void testIOExceptionWhileSelect() throws IOException {
        IOException ioException = new IOException();

        when(rawSelector.select(anyInt())).thenThrow(ioException);

        this.selector.singleLoop();

        verify(handler).selectException(ioException);
    }

    public void testSelectorClosedIfOpenAndEventLoopNotRunning() throws IOException {
        when(rawSelector.isOpen()).thenReturn(true);
        selector.close();
        verify(rawSelector).close();
    }

    private static class TestSelector extends ESSelector {

        TestSelector(EventHandler eventHandler, Selector selector) throws IOException {
            super(eventHandler, selector);
        }

        @Override
        void processKey(SelectionKey selectionKey) throws CancelledKeyException {

        }

        @Override
        void preSelect() {

        }

        @Override
        void cleanup() {

        }
    }

}
