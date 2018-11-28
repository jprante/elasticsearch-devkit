package org.elasticsearch.testframework;

import com.carrotsearch.randomizedtesting.annotations.Listeners;
import com.carrotsearch.randomizedtesting.annotations.TimeoutSuite;

import org.apache.lucene.testframework.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.testframework.util.LuceneTestCase;
import org.apache.lucene.testframework.util.TimeUnits;
import org.elasticsearch.Version;
import org.elasticsearch.testframework.bootstrap.BootstrapForTesting;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.testframework.junit.listeners.ReproduceInfoPrinter;

/*
 * Basic test case for token streams. the assertion methods in this class will
 * run basic checks to enforce correct behavior of the token streams.
 */
@Listeners({
        ReproduceInfoPrinter.class
})
@TimeoutSuite(millis = TimeUnits.HOUR)
@LuceneTestCase.SuppressReproduceLine
@LuceneTestCase.SuppressSysoutChecks(bugUrl = "we log a lot on purpose")
public abstract class ESTokenStreamTestCase extends BaseTokenStreamTestCase {

    static {
        try {
            Class.forName("org.elasticsearch.testframework.ESTestCase");
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
        BootstrapForTesting.ensureInitialized();
    }

    public Settings.Builder newAnalysisSettingsBuilder() {
        return Settings.builder().put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT);
    }

}
