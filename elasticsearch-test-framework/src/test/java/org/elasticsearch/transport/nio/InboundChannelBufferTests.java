package org.elasticsearch.transport.nio;

import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.test.ESTestCase;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class InboundChannelBufferTests extends ESTestCase {

    private static final int PAGE_SIZE = BigArrays.PAGE_SIZE_IN_BYTES;
    private final Supplier<InboundChannelBuffer.Page> defaultPageSupplier = () ->
        new InboundChannelBuffer.Page(ByteBuffer.allocate(BigArrays.BYTE_PAGE_SIZE), () -> {});

    public void testNewBufferHasSinglePage() {
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(defaultPageSupplier);

        assertEquals(PAGE_SIZE, channelBuffer.getCapacity());
        assertEquals(PAGE_SIZE, channelBuffer.getRemaining());
        assertEquals(0, channelBuffer.getIndex());
    }

    public void testExpandCapacity() {
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(defaultPageSupplier);

        assertEquals(PAGE_SIZE, channelBuffer.getCapacity());
        assertEquals(PAGE_SIZE, channelBuffer.getRemaining());

        channelBuffer.ensureCapacity(PAGE_SIZE + 1);

        assertEquals(PAGE_SIZE * 2, channelBuffer.getCapacity());
        assertEquals(PAGE_SIZE * 2, channelBuffer.getRemaining());
    }

    public void testExpandCapacityMultiplePages() {
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(defaultPageSupplier);

        assertEquals(PAGE_SIZE, channelBuffer.getCapacity());

        int multiple = randomInt(80);
        channelBuffer.ensureCapacity(PAGE_SIZE + ((multiple * PAGE_SIZE) - randomInt(500)));

        assertEquals(PAGE_SIZE * (multiple + 1), channelBuffer.getCapacity());
        assertEquals(PAGE_SIZE * (multiple + 1), channelBuffer.getRemaining());
    }

    public void testExpandCapacityRespectsOffset() {
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(defaultPageSupplier);

        assertEquals(PAGE_SIZE, channelBuffer.getCapacity());
        assertEquals(PAGE_SIZE, channelBuffer.getRemaining());

        int offset = randomInt(300);

        channelBuffer.release(offset);

        assertEquals(PAGE_SIZE - offset, channelBuffer.getCapacity());
        assertEquals(PAGE_SIZE - offset, channelBuffer.getRemaining());

        channelBuffer.ensureCapacity(PAGE_SIZE + 1);

        assertEquals(PAGE_SIZE * 2 - offset, channelBuffer.getCapacity());
        assertEquals(PAGE_SIZE * 2 - offset, channelBuffer.getRemaining());
    }

    public void testIncrementIndex() {
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(defaultPageSupplier);

        assertEquals(0, channelBuffer.getIndex());
        assertEquals(PAGE_SIZE, channelBuffer.getRemaining());

        channelBuffer.incrementIndex(10);

        assertEquals(10, channelBuffer.getIndex());
        assertEquals(PAGE_SIZE - 10, channelBuffer.getRemaining());
    }

    public void testIncrementIndexWithOffset() {
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(defaultPageSupplier);

        assertEquals(0, channelBuffer.getIndex());
        assertEquals(PAGE_SIZE, channelBuffer.getRemaining());

        channelBuffer.release(10);
        assertEquals(PAGE_SIZE - 10, channelBuffer.getRemaining());

        channelBuffer.incrementIndex(10);

        assertEquals(10, channelBuffer.getIndex());
        assertEquals(PAGE_SIZE - 20, channelBuffer.getRemaining());

        channelBuffer.release(2);
        assertEquals(8, channelBuffer.getIndex());
        assertEquals(PAGE_SIZE - 20, channelBuffer.getRemaining());
    }

    public void testReleaseClosesPages() {
        ConcurrentLinkedQueue<AtomicBoolean> queue = new ConcurrentLinkedQueue<>();
        Supplier<InboundChannelBuffer.Page> supplier = () -> {
            AtomicBoolean atomicBoolean = new AtomicBoolean();
            queue.add(atomicBoolean);
            return new InboundChannelBuffer.Page(ByteBuffer.allocate(PAGE_SIZE), () -> atomicBoolean.set(true));
        };
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(supplier);
        channelBuffer.ensureCapacity(PAGE_SIZE * 4);

        assertEquals(PAGE_SIZE * 4, channelBuffer.getCapacity());
        assertEquals(4, queue.size());

        for (AtomicBoolean closedRef : queue) {
            assertFalse(closedRef.get());
        }

        channelBuffer.release(2 * PAGE_SIZE);

        assertEquals(PAGE_SIZE * 2, channelBuffer.getCapacity());

        assertTrue(queue.poll().get());
        assertTrue(queue.poll().get());
        assertFalse(queue.poll().get());
        assertFalse(queue.poll().get());
    }

    public void testClose() {
        ConcurrentLinkedQueue<AtomicBoolean> queue = new ConcurrentLinkedQueue<>();
        Supplier<InboundChannelBuffer.Page> supplier = () -> {
            AtomicBoolean atomicBoolean = new AtomicBoolean();
            queue.add(atomicBoolean);
            return new InboundChannelBuffer.Page(ByteBuffer.allocate(PAGE_SIZE), () -> atomicBoolean.set(true));
        };
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(supplier);
        channelBuffer.ensureCapacity(PAGE_SIZE * 4);

        assertEquals(4, queue.size());

        for (AtomicBoolean closedRef : queue) {
            assertFalse(closedRef.get());
        }

        channelBuffer.close();

        for (AtomicBoolean closedRef : queue) {
            assertTrue(closedRef.get());
        }

        expectThrows(IllegalStateException.class, () -> channelBuffer.ensureCapacity(1));
    }

    public void testAccessByteBuffers() {
        InboundChannelBuffer channelBuffer = new InboundChannelBuffer(defaultPageSupplier);

        int pages = randomInt(50) + 5;
        channelBuffer.ensureCapacity(pages * PAGE_SIZE);

        long capacity = channelBuffer.getCapacity();

        ByteBuffer[] postIndexBuffers = channelBuffer.sliceBuffersFrom(channelBuffer.getIndex());
        int i = 0;
        for (ByteBuffer buffer : postIndexBuffers) {
            while (buffer.hasRemaining()) {
                buffer.put((byte) (i++ % 127));
            }
        }

        int indexIncremented = 0;
        int bytesReleased = 0;
        while (indexIncremented < capacity) {
            assertEquals(indexIncremented - bytesReleased, channelBuffer.getIndex());

            long amountToInc = Math.min(randomInt(2000), channelBuffer.getRemaining());
            ByteBuffer[] postIndexBuffers2 = channelBuffer.sliceBuffersFrom(channelBuffer.getIndex());
            assertEquals((byte) ((channelBuffer.getIndex() + bytesReleased) % 127), postIndexBuffers2[0].get());
            ByteBuffer[] preIndexBuffers = channelBuffer.sliceBuffersTo(channelBuffer.getIndex());
            if (preIndexBuffers.length > 0) {
                ByteBuffer preIndexBuffer = preIndexBuffers[preIndexBuffers.length - 1];
                assertEquals((byte) ((channelBuffer.getIndex() + bytesReleased - 1) % 127), preIndexBuffer.get(preIndexBuffer.limit() - 1));
            }
            if (randomBoolean()) {
                long bytesToRelease = Math.min(randomInt(50), channelBuffer.getIndex());
                channelBuffer.release(bytesToRelease);
                bytesReleased += bytesToRelease;
            }
            channelBuffer.incrementIndex(amountToInc);
            indexIncremented += amountToInc;
        }

        assertEquals(0, channelBuffer.sliceBuffersFrom(channelBuffer.getIndex()).length);
    }
}
