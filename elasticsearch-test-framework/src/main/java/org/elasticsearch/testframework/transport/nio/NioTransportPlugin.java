package org.elasticsearch.testframework.transport.nio;

import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.common.util.PageCacheRecycler;
import org.elasticsearch.indices.breaker.CircuitBreakerService;
import org.elasticsearch.plugins.NetworkPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.Transport;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public class NioTransportPlugin extends Plugin implements NetworkPlugin {

    public static final String NIO_TRANSPORT_NAME = "nio-transport";

    @Override
    public Map<String, Supplier<Transport>> getTransports(Settings settings, ThreadPool threadPool, BigArrays bigArrays,
                                                          PageCacheRecycler pageCacheRecycler,
                                                          CircuitBreakerService circuitBreakerService,
                                                          NamedWriteableRegistry namedWriteableRegistry,
                                                          NetworkService networkService) {
        Settings settings1;
        if (!NioTransport.NIO_WORKER_COUNT.exists(settings)) {
            // As this is only used for tests right now, limit the number of worker threads.
            settings1 = Settings.builder().put(settings).put(NioTransport.NIO_WORKER_COUNT.getKey(), 2).build();
        } else {
            settings1 = settings;
        }
        return Collections.singletonMap(NIO_TRANSPORT_NAME,
            () -> new NioTransport(settings1, threadPool, networkService, bigArrays, pageCacheRecycler, namedWriteableRegistry,
                circuitBreakerService));
    }
}
