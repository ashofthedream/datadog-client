package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;


public class Counter extends Metric<Counter> {

    public Counter(DatadogClient client, String name) {
        super(client, name);
    }

    /**
     * Adjusts counter by a given value
     *
     * @param value counter value
     */
    public void count(long value) {
        client.count(name, value, tags);
    }

    /**
     * Increments the specified counter by one
     */
    public void inc() {
        count(1);
    }

    /**
     * Decrements the specified counter by one
     */
    public void dec() {
        count(-1);
    }
}
