package org.elasticsearch.index.reindex;

import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.threadpool.TestThreadPool;
import org.elasticsearch.threadpool.ThreadPool;
import org.junit.After;
import org.junit.Before;

import java.util.Collections;

public abstract class AbstractAsyncBulkByScrollActionTestCase<
                Request extends AbstractBulkByScrollRequest<Request>,
                Response extends BulkByScrollResponse>
        extends ESTestCase {
    protected ThreadPool threadPool;
    protected BulkByScrollTask task;

    @Before
    public void setupForTest() {
        threadPool = new TestThreadPool(getTestName());
        task = new BulkByScrollTask(1, "test", "test", "test", TaskId.EMPTY_TASK_ID, Collections.emptyMap());
        task.setWorker(Float.POSITIVE_INFINITY, null);

    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        threadPool.shutdown();
    }

    protected abstract Request request();

    protected PlainActionFuture<Response> listener() {
        return new PlainActionFuture<>();
    }
}
