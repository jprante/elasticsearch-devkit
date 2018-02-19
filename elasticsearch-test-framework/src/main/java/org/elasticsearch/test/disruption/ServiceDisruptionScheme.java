package org.elasticsearch.test.disruption;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.test.InternalTestCluster;

public interface ServiceDisruptionScheme {

    void applyToCluster(InternalTestCluster cluster);

    void removeFromCluster(InternalTestCluster cluster);

    void removeAndEnsureHealthy(InternalTestCluster cluster);

    void applyToNode(String node, InternalTestCluster cluster);

    void removeFromNode(String node, InternalTestCluster cluster);

    void startDisrupting();

    void stopDisrupting();

    void testClusterClosed();

    TimeValue expectedTimeToHeal();

}
