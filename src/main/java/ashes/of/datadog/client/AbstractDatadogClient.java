package ashes.of.datadog.client;

import ashes.of.datadog.client.metrics.MetricType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static ashes.of.datadog.client.metrics.MetricType.*;


public abstract class AbstractDatadogClient implements DatadogClient {

    @Nullable
    protected final String prefix;

    protected final Consumer<Throwable> errorHandler;
    protected final Tags global;

    public AbstractDatadogClient(DatadogBuilder b) {
        this.prefix = b.getPrefix();
        this.errorHandler = b.getErrorHandler();
        this.global = b.tags();
    }

    protected void send(String metric, long value, MetricType type, Tags tags) {
        send(metric, String.valueOf(value), type, tags);
    }

    protected void send(String metric, double value, MetricType type, Tags tags) {
        send(metric, String.valueOf(value), type, tags);
    }

    protected abstract void send(String metric, String value, MetricType type, Tags tags);


    @Override
    public void count(String metric, long value, Tags tags) {
        send(metric, value, COUNTER, tags);
    }


    @Override
    public void gauge(String metric, double value, Tags tags) {
        send(metric, value, GAUGE, tags);
    }

    @Override
    public void gauge(String metric, long value, Tags tags) {
        send(metric, value, GAUGE, tags);
    }


    @Override
    public void histogram(String metric, double value, Tags tags) {
        send(metric, value, HISTOGRAM, tags);
    }

    @Override
    public void histogram(String metric, long value, Tags tags) {
        send(metric, value, HISTOGRAM, tags);
    }


    @Override
    public void set(String metric, String value, Tags tags) {
        send(metric, value, SET, tags);
    }

    @Override
    public void set(String metric, long value, Tags tags) {
        send(metric, value, SET, tags);
    }
}
