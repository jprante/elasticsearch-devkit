package org.elasticsearch.testframework.threadpool;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.threadpool.ThreadPool;

public class TestThreadPool extends ThreadPool {

    public TestThreadPool(String name) {
        this(name, Settings.EMPTY);
    }

    public TestThreadPool(String name, Settings settings) {
        super(Settings.builder().put(Node.NODE_NAME_SETTING.getKey(), name).put(settings).build());
    }

}
