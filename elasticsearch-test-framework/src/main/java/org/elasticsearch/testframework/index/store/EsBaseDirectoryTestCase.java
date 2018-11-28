package org.elasticsearch.testframework.index.store;

import com.carrotsearch.randomizedtesting.annotations.Listeners;
import com.carrotsearch.randomizedtesting.annotations.TimeoutSuite;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.testframework.store.BaseDirectoryTestCase;
import org.apache.lucene.testframework.util.LuceneTestCase;
import org.apache.lucene.testframework.util.TimeUnits;
import org.elasticsearch.testframework.bootstrap.BootstrapForTesting;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.testframework.junit.listeners.ReproduceInfoPrinter;

/**
 * Extends Lucene's BaseDirectoryTestCase with ES test behavior.
 */
@Listeners({
  ReproduceInfoPrinter.class
})
@TimeoutSuite(millis = TimeUnits.HOUR)
@LuceneTestCase.SuppressReproduceLine
@LuceneTestCase.SuppressSysoutChecks(bugUrl = "we log a lot on purpose")
public abstract class EsBaseDirectoryTestCase extends BaseDirectoryTestCase {
    static {
        try {
            Class.forName("org.elasticsearch.testframework.ESTestCase");
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
        BootstrapForTesting.ensureInitialized();
    }

    protected final Logger logger = Loggers.getLogger(getClass());

}
