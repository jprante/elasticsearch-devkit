package org.elasticsearch.test;

import org.elasticsearch.cluster.Diff;
import org.elasticsearch.cluster.Diffable;
import org.elasticsearch.common.io.stream.Writeable.Reader;

import java.io.IOException;

/**
 * An abstract test case to ensure correct behavior of Diffable.
 *
 * This class can be used as a based class for tests of ClusterState.Custom classes and other classes that support,
 * Writable serialization and is diffable.
 */
public abstract class AbstractDiffableWireSerializationTestCase<T extends Diffable<T>> extends AbstractWireSerializingTestCase<T> {
    /**
     *  Introduces random changes into the test object
     */
    protected abstract T makeTestChanges(T testInstance);

    protected abstract Reader<Diff<T>> diffReader();

    public final void testDiffableSerialization() throws IOException {
        DiffableTestUtils.testDiffableSerialization(this::createTestInstance, this::makeTestChanges, getNamedWriteableRegistry(),
            instanceReader(), diffReader());
    }

}
