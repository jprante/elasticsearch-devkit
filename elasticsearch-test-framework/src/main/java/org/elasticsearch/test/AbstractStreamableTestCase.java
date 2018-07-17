package org.elasticsearch.test;

import org.elasticsearch.Version;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.io.stream.Writeable;

import java.io.IOException;

public abstract class AbstractStreamableTestCase<T extends Streamable> extends AbstractWireTestCase<T> {

    @Override
    protected final T copyInstance(T instance, Version version) throws IOException {
        return copyStreamable(instance, getNamedWriteableRegistry(), this::createBlankInstance, version);
    }

    @Override
    protected final Writeable.Reader<T> instanceReader() {
        return Streamable.newWriteableReader(this::createBlankInstance);
    }

    /**
     * Creates an empty instance to use when deserialising the
     * {@link Streamable}. This usually returns an instance created using the
     * zer-arg constructor
     */
    protected abstract T createBlankInstance();

}
