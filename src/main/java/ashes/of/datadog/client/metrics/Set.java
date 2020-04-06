package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;


public class Set extends Metric<Set> {

    public Set(DatadogClient client, String name) {
        super(client, name);
    }


    /**
     * Records a value for the set
     *
     * @param value value for set
     */
    public void add(long value) {
        client.set(name, value, tags);
    }

    /**
     * Records a value for the set
     *
     * @param value value for set
     */
    public void add(String value) {
        client.set(name, value, tags);
    }
}
