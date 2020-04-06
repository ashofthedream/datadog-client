package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;


/**
 * Gauges measure the value of a particular thing over time
 */
public class Gauge extends Metric<Gauge> {

    public Gauge(DatadogClient client, String time) {
        super(client, time);
    }

    /**
     * Records a value for the gauge
     *
     * @param value value for gauge
     */
    public void value(long value) {
        client.gauge(name, value, tags);
    }

    /**
     * Records a value for the gauge
     *
     * @param value value for gauge
     */
    public void value(double value) {
        client.gauge(name, value, tags);
    }
}
