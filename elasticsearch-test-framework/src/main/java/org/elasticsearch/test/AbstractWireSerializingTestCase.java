package org.elasticsearch.test;

import org.elasticsearch.Version;
import org.elasticsearch.common.io.stream.Writeable;

import java.io.IOException;

public abstract class AbstractWireSerializingTestCase<T extends Writeable> extends AbstractWireTestCase<T> {

    protected T copyInstance(T instance, Version version) throws IOException {
        return copyWriteable(instance, getNamedWriteableRegistry(), instanceReader());
    }
}
