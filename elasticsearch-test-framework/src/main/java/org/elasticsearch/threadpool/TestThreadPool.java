package org.elasticsearch.threadpool;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

public class TestThreadPool extends ThreadPool {

    public TestThreadPool(String name) {
        this(name, Settings.EMPTY);
    }

    public TestThreadPool(String name, Settings settings) {
        super(Settings.builder().put(Node.NODE_NAME_SETTING.getKey(), name).put(settings).build());
    }

}
