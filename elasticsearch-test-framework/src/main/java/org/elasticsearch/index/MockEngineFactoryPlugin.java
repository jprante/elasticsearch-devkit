package org.elasticsearch.index;

import org.apache.lucene.index.AssertingDirectoryReader;
import org.apache.lucene.index.FilterDirectoryReader;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.engine.MockEngineFactory;
import org.elasticsearch.test.engine.MockEngineSupport;

import java.util.Arrays;
import java.util.List;

/**
 * A plugin to use {@link MockEngineFactory}.
 *
 * Subclasses may override the reader wrapper used.
 */
public class MockEngineFactoryPlugin extends Plugin {

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(MockEngineSupport.DISABLE_FLUSH_ON_CLOSE, MockEngineSupport.WRAP_READER_RATIO);
    }

    @Override
    public void onIndexModule(IndexModule module) {
        module.engineFactory.set(new MockEngineFactory(getReaderWrapperClass()));
    }

    protected Class<? extends FilterDirectoryReader> getReaderWrapperClass() {
        return AssertingDirectoryReader.class;
    }
}
