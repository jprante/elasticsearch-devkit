package org.elasticsearch.testframework.disruption;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.testframework.InternalTestCluster;

public class NoOpDisruptionScheme implements ServiceDisruptionScheme {

    @Override
    public void applyToCluster(InternalTestCluster cluster) {

    }

    @Override
    public void removeFromCluster(InternalTestCluster cluster) {

    }

    @Override
    public void applyToNode(String node, InternalTestCluster cluster) {

    }

    @Override
    public void removeFromNode(String node, InternalTestCluster cluster) {

    }

    @Override
    public void startDisrupting() {

    }

    @Override
    public void stopDisrupting() {

    }

    @Override
    public void testClusterClosed() {

    }

    @Override
    public void removeAndEnsureHealthy(InternalTestCluster cluster) {

    }

    @Override
    public TimeValue expectedTimeToHeal() {
        return TimeValue.timeValueSeconds(0);
    }
}
