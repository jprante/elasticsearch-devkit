package org.elasticsearch.testframework.test.transport.nio;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.testframework.transport.nio.SocketEventHandler;
import org.elasticsearch.testframework.transport.nio.channel.NioSocketChannel;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class TestingSocketEventHandler extends SocketEventHandler {

    public TestingSocketEventHandler(Logger logger) {
        super(logger);
    }

    private Set<NioSocketChannel> hasConnectedMap = Collections.newSetFromMap(new WeakHashMap<>());

    public void handleConnect(NioSocketChannel channel) {
        assert !hasConnectedMap.contains(channel) : "handleConnect should only be called once per channel";
        hasConnectedMap.add(channel);
        super.handleConnect(channel);
    }

    private Set<NioSocketChannel> hasConnectExceptionMap = Collections.newSetFromMap(new WeakHashMap<>());

    public void connectException(NioSocketChannel channel, Exception e) {
        assert !hasConnectExceptionMap.contains(channel) : "connectException should only called at maximum once per channel";
        hasConnectExceptionMap.add(channel);
        super.connectException(channel, e);
    }

    public void handleRead(NioSocketChannel channel) throws IOException {
        super.handleRead(channel);
    }

    public void readException(NioSocketChannel channel, Exception e) {
        super.readException(channel, e);
    }

    public void handleWrite(NioSocketChannel channel) throws IOException {
        super.handleWrite(channel);
    }

    public void writeException(NioSocketChannel channel, Exception e) {
        super.writeException(channel, e);
    }

}
