package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogBuilder;
import ashes.of.datadog.client.DatadogClient;
import ashes.of.datadog.client.DisruptorDatadogClient;
import ashes.of.datadog.server.DatadogServer;
import org.junit.After;
import org.junit.Before;

import java.net.InetSocketAddress;



public abstract class MetricsTest {

    private static final InetSocketAddress address = new InetSocketAddress("localhost", 31337);

    protected DatadogServer server;
    protected DatadogClient withPrefixAndTags, noPrefixAndTags;


    @Before
    public void setUp() {
        server = new DatadogServer(address);
        server.start();

        withPrefixAndTags = new DatadogBuilder()
                .address(address)
                .prefix("test")
                .tag("env", "junit")
                .build(DisruptorDatadogClient::new);

        noPrefixAndTags = new DatadogBuilder()
                .address(address)
                .build(DisruptorDatadogClient::new);
    }

    @After
    public void shutDown() {
        withPrefixAndTags.stop();
        noPrefixAndTags.stop();
        server.stop();
    }
}
