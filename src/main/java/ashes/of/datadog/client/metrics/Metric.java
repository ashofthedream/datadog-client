package ashes.of.datadog.client.metrics;


import ashes.of.datadog.client.DatadogClient;
import ashes.of.datadog.client.Taggable;
import ashes.of.datadog.client.Tags;

import java.util.function.Supplier;


public abstract class Metric<M extends Metric<M>> implements Taggable<M> {
    protected final DatadogClient client;
    protected final String name;
    protected final Tags tags = new Tags();

    public Metric(DatadogClient client, String name) {
        this.client = client;
        this.name = name;
    }

    @Override
    public M tag(String tag, Supplier<Object> sub) {
        tags.tag(tag, sub);
        return (M) this;
    }

    @Override
    public Tags tags() {
        return tags;
    }
}
