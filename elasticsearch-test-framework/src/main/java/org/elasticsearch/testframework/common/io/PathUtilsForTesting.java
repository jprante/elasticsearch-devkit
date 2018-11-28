package org.elasticsearch.testframework.common.io;

import org.apache.lucene.testframework.util.LuceneTestCase;
import org.elasticsearch.common.io.PathUtils;

import java.nio.file.FileSystem;

/**
 * Exposes some package private stuff in PathUtils for framework purposes only.
 */
public class PathUtilsForTesting {

    /** Sets a new default filesystem for testing */
    public static void setup() {
        installMock(LuceneTestCase.createTempDir().getFileSystem());
    }

    /** Installs a mock filesystem for testing */
    public static void installMock(FileSystem mock) {
        PathUtils.DEFAULT = mock;
    }

    /** Resets filesystem back to the real system default */
    public static void teardown() {
        PathUtils.DEFAULT = PathUtils.ACTUAL_DEFAULT;
    }
}
