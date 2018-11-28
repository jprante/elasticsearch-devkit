package org.elasticsearch.testframework.transport.nio;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.elasticsearch.testframework.transport.nio.channel.NioChannel;

import java.io.IOException;
import java.nio.channels.Selector;

public abstract class EventHandler {

    protected final Logger logger;

    public EventHandler(Logger logger) {
        this.logger = logger;
    }

    /**
     * This method handles an IOException that was thrown during a call to {@link Selector#select(long)}.
     *
     * @param exception the exception
     */
    public void selectException(IOException exception) {
        logger.warn(new ParameterizedMessage("io exception during select [thread={}]", Thread.currentThread().getName()), exception);
    }

    /**
     * This method handles an IOException that was thrown during a call to {@link Selector#close()}.
     *
     * @param exception the exception
     */
    public void closeSelectorException(IOException exception) {
        logger.warn(new ParameterizedMessage("io exception while closing selector [thread={}]", Thread.currentThread().getName()),
            exception);
    }

    /**
     * This method handles an exception that was uncaught during a select loop.
     *
     * @param exception that was uncaught
     */
    public void uncaughtException(Exception exception) {
        Thread thread = Thread.currentThread();
        thread.getUncaughtExceptionHandler().uncaughtException(thread, exception);
    }

    /**
     * This method handles the closing of an NioChannel
     *
     * @param channel that should be closed
     */
    public void handleClose(NioChannel channel) {
        try {
            channel.closeFromSelector();
        } catch (IOException e) {
            closeException(channel, e);
        }
        assert !channel.isOpen() : "Should always be done as we are on the selector thread";
    }

    /**
     * This method is called when an attempt to close a channel throws an exception.
     *
     * @param channel that was being closed
     * @param exception that occurred
     */
    public void closeException(NioChannel channel, Exception exception) {
        logger.debug(() -> new ParameterizedMessage("exception while closing channel: {}", channel), exception);
    }

    /**
     * This method is called when handling an event from a channel fails due to an unexpected exception.
     * An example would be if checking ready ops on a {@link java.nio.channels.SelectionKey} threw
     * {@link java.nio.channels.CancelledKeyException}.
     *
     * @param channel that caused the exception
     * @param exception that was thrown
     */
    public void genericChannelException(NioChannel channel, Exception exception) {
        logger.debug(() -> new ParameterizedMessage("exception while handling event for channel: {}", channel), exception);
    }
}
