package org.elasticsearch.testframework.cluster.routing;

import org.elasticsearch.cluster.routing.RecoverySource;
import org.elasticsearch.cluster.routing.RecoverySource.SnapshotRecoverySource;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.ShardRoutingState;
import org.elasticsearch.cluster.routing.UnassignedInfo;

/**
 * A helper class that allows access to package private APIs for testing.
 */
public class ShardRoutingHelper {

    public static ShardRouting relocate(ShardRouting routing, String nodeId) {
        return relocate(routing, nodeId, ShardRouting.UNAVAILABLE_EXPECTED_SHARD_SIZE);
    }

    public static ShardRouting relocate(ShardRouting routing, String nodeId, long expectedByteSize) {
        return routing.relocate(nodeId, expectedByteSize);
    }

    public static ShardRouting moveToStarted(ShardRouting routing) {
        return routing.moveToStarted();
    }

    public static ShardRouting initialize(ShardRouting routing, String nodeId) {
        return initialize(routing, nodeId, ShardRouting.UNAVAILABLE_EXPECTED_SHARD_SIZE);
    }

    public static ShardRouting initialize(ShardRouting routing, String nodeId, long expectedSize) {
        return routing.initialize(nodeId, null, expectedSize);
    }

    public static ShardRouting initWithSameId(ShardRouting copy, RecoverySource recoverySource) {
        return new ShardRouting(copy.shardId(), copy.currentNodeId(), copy.relocatingNodeId(),
            copy.primary(), ShardRoutingState.INITIALIZING, recoverySource, new UnassignedInfo(UnassignedInfo.Reason.REINITIALIZED, null),
            copy.allocationId(), copy.getExpectedShardSize());
    }

    public static ShardRouting moveToUnassigned(ShardRouting routing, UnassignedInfo info) {
        return routing.moveToUnassigned(info);
    }

    public static ShardRouting newWithRestoreSource(ShardRouting routing, SnapshotRecoverySource recoverySource) {
        return new ShardRouting(routing.shardId(), routing.currentNodeId(), routing.relocatingNodeId(), routing.primary(), routing.state(),
            recoverySource, routing.unassignedInfo(), routing.allocationId(), routing.getExpectedShardSize());
    }
}