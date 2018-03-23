package ashes.of.datadog.client;

import ashes.of.datadog.client.metrics.Event;
import ashes.of.datadog.client.metrics.MetricType;
import ashes.of.datadog.client.metrics.ServiceCheck;


/**
 * A mock client, which can be substituted in when metrics are not
 * required.
 */
public class MockDatadogClient extends AbstractDatadogClient {


    public MockDatadogClient(DatadogBuilder b) {
        super(b);
    }

    public MockDatadogClient() {
        super(new DatadogBuilder());
    }

    @Override
    public void stop() {}

    @Override
    protected void send(String metric, String value, MetricType type, String... tags) {}

    @Override
    public void send(Event event) {}

    @Override
    public void send(ServiceCheck check) {}
}
