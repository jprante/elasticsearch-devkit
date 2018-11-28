package org.elasticsearch.testframework.test.transport.nio;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.indices.breaker.NoneCircuitBreakerService;
import org.elasticsearch.testframework.transport.AbstractSimpleTransportTestCase;
import org.elasticsearch.testframework.transport.MockTcpTransport;
import org.elasticsearch.testframework.transport.MockTransportService;
import org.elasticsearch.transport.TcpChannel;
import org.elasticsearch.transport.TcpTransport;
import org.elasticsearch.transport.Transport;

import java.io.IOException;
import java.util.Collections;

public class MockTcpTransportTests extends AbstractSimpleTransportTestCase {

    @Override
    protected MockTransportService build(Settings settings, Version version, ClusterSettings clusterSettings, boolean doHandshake) {
        NamedWriteableRegistry namedWriteableRegistry = new NamedWriteableRegistry(Collections.emptyList());
        Transport transport = new MockTcpTransport(settings, threadPool, BigArrays.NON_RECYCLING_INSTANCE,
            new NoneCircuitBreakerService(), namedWriteableRegistry, new NetworkService(Collections.emptyList()), version) {
            @Override
            public Version executeHandshake(DiscoveryNode node, TcpChannel mockChannel, TimeValue timeout) throws IOException,
                InterruptedException {
                if (doHandshake) {
                    return super.executeHandshake(node, mockChannel, timeout);
                } else {
                    return version.minimumCompatibilityVersion();
                }
            }
        };
        MockTransportService mockTransportService =
            MockTransportService.createNewService(Settings.EMPTY, transport, version, threadPool, clusterSettings, Collections.emptySet());
        mockTransportService.start();
        return mockTransportService;
    }

    @Override
    public int channelsPerNodeConnection() {
        return 1;
    }

    @Override
    protected void closeConnectionChannel(Transport transport, Transport.Connection connection) throws IOException {
        final MockTcpTransport t = (MockTcpTransport) transport;
        @SuppressWarnings("unchecked") final TcpTransport.NodeChannels channels =
                (TcpTransport.NodeChannels) connection;
        TcpChannel.closeChannels(channels.getChannels().subList(0, randomIntBetween(1, channels.getChannels().size())), true);
    }

}
