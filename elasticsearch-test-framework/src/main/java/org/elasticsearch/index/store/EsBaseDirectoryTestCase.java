package org.elasticsearch.index.store;

import com.carrotsearch.randomizedtesting.annotations.Listeners;
import com.carrotsearch.randomizedtesting.annotations.TimeoutSuite;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.store.BaseDirectoryTestCase;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.TimeUnits;
import org.elasticsearch.bootstrap.BootstrapForTesting;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.test.junit.listeners.ReproduceInfoPrinter;

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
            Class.forName("org.elasticsearch.test.ESTestCase");
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
        BootstrapForTesting.ensureInitialized();
    }

    protected final Logger logger = Loggers.getLogger(getClass());

}
