package org.elasticsearch.transport.nio;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class RoundRobinSupplier<S> implements Supplier<S> {

    private final S[] selectors;
    private final int count;
    private AtomicInteger counter = new AtomicInteger(0);

    public RoundRobinSupplier(S[] selectors) {
        this.count = selectors.length;
        this.selectors = selectors;
    }

    public S get() {
        return selectors[counter.getAndIncrement() % count];
    }
}
