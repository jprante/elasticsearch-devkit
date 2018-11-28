package org.elasticsearch.testframework.test.transport.nio;

import org.elasticsearch.common.CheckedRunnable;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.testframework.ESTestCase;
import org.elasticsearch.testframework.transport.nio.AcceptorEventHandler;
import org.elasticsearch.testframework.transport.nio.NioGroup;
import org.elasticsearch.testframework.transport.nio.SocketEventHandler;
import org.elasticsearch.testframework.transport.nio.channel.ChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.elasticsearch.common.util.concurrent.EsExecutors.daemonThreadFactory;
import static org.mockito.Mockito.mock;

public class NioGroupTests extends ESTestCase {

    private NioGroup nioGroup;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        nioGroup = new NioGroup(logger, daemonThreadFactory(Settings.EMPTY, "acceptor"), 1, AcceptorEventHandler::new,
            daemonThreadFactory(Settings.EMPTY, "selector"), 1, SocketEventHandler::new);
    }

    @Override
    public void tearDown() throws Exception {
        nioGroup.close();
        super.tearDown();
    }

    public void testStartAndClose() throws IOException {
        // ctor starts threads. So we are testing that close() stops the threads. Our thread linger checks
        // will throw an exception is stop fails
        nioGroup.close();
    }

    @SuppressWarnings("unchecked")
    public void testCannotOperateAfterClose() throws IOException {
        nioGroup.close();

        IllegalStateException ise = expectThrows(IllegalStateException.class,
            () -> nioGroup.bindServerChannel(mock(InetSocketAddress.class), mock(ChannelFactory.class)));
        assertEquals("NioGroup is closed.", ise.getMessage());
        ise = expectThrows(IllegalStateException.class,
            () -> nioGroup.openChannel(mock(InetSocketAddress.class), mock(ChannelFactory.class)));
        assertEquals("NioGroup is closed.", ise.getMessage());
    }

    public void testCanCloseTwice() throws IOException {
        nioGroup.close();
        nioGroup.close();
    }

    public void testExceptionAtStartIsHandled() throws IOException {
        RuntimeException ex = new RuntimeException();
        CheckedRunnable<IOException> ctor = () -> new NioGroup(logger, r -> {throw ex;}, 1,
            AcceptorEventHandler::new, daemonThreadFactory(Settings.EMPTY, "selector"), 1, SocketEventHandler::new);
        RuntimeException runtimeException = expectThrows(RuntimeException.class, ctor::run);
        assertSame(ex, runtimeException);
        // ctor starts threads. So we are testing that a failure to construct will stop threads. Our thread
        // linger checks will throw an exception is stop fails
    }
}
