package org.elasticsearch.test.engine;

import org.apache.lucene.index.FilterDirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.elasticsearch.index.engine.EngineConfig;
import org.elasticsearch.index.engine.EngineException;
import org.elasticsearch.index.engine.InternalEngine;

import java.io.IOException;

final class MockInternalEngine extends InternalEngine {
    private MockEngineSupport support;
    private Class<? extends FilterDirectoryReader> wrapperClass;

    MockInternalEngine(EngineConfig config,  Class<? extends FilterDirectoryReader> wrapper) throws EngineException {
        super(config);
        wrapperClass = wrapper;

    }

    private synchronized MockEngineSupport support() {
        // lazy initialized since we need it already on super() ctor execution :(
        if (support == null) {
            support = new MockEngineSupport(config(), wrapperClass);
        }
        return support;
    }

    @Override
    public void close() throws IOException {
        switch (support().flushOrClose(MockEngineSupport.CloseAction.CLOSE)) {
            case FLUSH_AND_CLOSE:
                flushAndCloseInternal();
                break;
            case CLOSE:
                super.close();
                break;
        }
    }

    @Override
    public void flushAndClose() throws IOException {
        switch (support().flushOrClose(MockEngineSupport.CloseAction.FLUSH_AND_CLOSE)) {
            case FLUSH_AND_CLOSE:
                flushAndCloseInternal();
                break;
            case CLOSE:
                super.close();
                break;
        }
    }

    private void flushAndCloseInternal() throws IOException {
        if (support().isFlushOnCloseDisabled() == false) {
            super.flushAndClose();
        } else {
            super.close();
        }
    }

    @Override
    protected Searcher newSearcher(String source, IndexSearcher searcher, ReferenceManager<IndexSearcher> manager) throws EngineException {
        final Searcher engineSearcher = super.newSearcher(source, searcher, manager);
        return support().wrapSearcher(source, engineSearcher, searcher, manager);
    }
}
