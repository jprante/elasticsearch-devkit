package org.elasticsearch.testframework.gateway;

import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.allocation.FailedShard;
import org.elasticsearch.cluster.routing.allocation.RoutingAllocation;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.gateway.GatewayAllocator;

import java.util.List;

/**
 * An allocator used for tests that doesn't do anything.
 */
public class NoopGatewayAllocator extends GatewayAllocator {

    public static final NoopGatewayAllocator INSTANCE = new NoopGatewayAllocator();

    protected NoopGatewayAllocator() {
        super(Settings.EMPTY);
    }

    @Override
    public void applyStartedShards(RoutingAllocation allocation, List<ShardRouting> startedShards) {
        // noop
    }

    @Override
    public void applyFailedShards(RoutingAllocation allocation, List<FailedShard> failedShards) {
        // noop
    }

    @Override
    public void allocateUnassigned(RoutingAllocation allocation) {
        // noop
    }
}
