package ashes.of.datadog.client;

import ashes.of.datadog.client.metrics.*;
import ashes.of.datadog.client.metrics.ServiceCheck.Status;


/**
 * Client connection to a datadog local agent, which may be used to post metrics
 * in the form of counters, timers, gauges, events and service checks.
 */
public interface DatadogClient {

    /**
     * Cleanly shut down this client.
     */
    void stop();


    /**
     * Adjusts counter by a given value
     *
     * @param metric counter name
     * @param value counter value
     * @param tags additional tags
     */
    default void count(String metric, long value, String... tags) {
        count(metric, value, new Tags().tags(tags));
    }

    void count(String metric, long value, Tags tags);

    /**
     * Increments the specified counter by one
     *
     * @param metric counter name
     * @param tags additional tags
     */
    default void increment(String metric, String... tags) {
        count(metric, 1, tags);
    }

    default void increment(String metric, Tags tags) {
        count(metric, 1, tags);
    }

    /**
     * Decrements the specified counter by one
     *
     * @param metric counter name
     * @param tags additional tags
     */
    default void decrement(String metric, String... tags) {
        count(metric, -1, tags);
    }

    default void decrement(String metric, Tags tags) {
        count(metric, -1, tags);
    }

    /**
     * @param metric counter name
     * @return new counter
     */
    default Counter counter(String metric) {
        return new Counter(this, metric);
    }


    /**
     * Records the latest fixed value
     *
     * @param metric gauge name
     * @param value gauge value
     * @param tags additional tags
     */
    default void gauge(String metric, double value, String... tags) {
        gauge(metric, value, new Tags().tags(tags));
    }

    void gauge(String metric, double value, Tags tags);

    /**
     * Records the latest fixed value for the specified named gauge
     *
     * @param metric gauge name
     * @param value gauge value
     * @param tags additional tags
     */
    default void gauge(String metric, long value, String... tags) {
        gauge(metric, value, new Tags().tags(tags));
    }

    void gauge(String metric, long value, Tags tags);

    /**
     * @param metric gauge name
     * @return new gauge
     */
    default Gauge gauge(String metric) {
        return new Gauge(this, metric);
    }


    /**
     * Records time in milliseconds
     *
     * @param metric timer name
     * @param millis time in milliseconds
     * @param tags additional tags
     */
    default void millis(String metric, long millis, String... tags) {
        histogram(metric, millis / 1_000., new Tags().tags(tags));
    }

    default void millis(String metric, long millis, Tags tags) {
        histogram(metric, millis / 1_000., tags);
    }

    /**
     * Records time in nanoseconds
     *
     * @param metric timer name
     * @param nanos time in nanoseconds
     * @param tags additional tags
     */
    default void nanos(String metric, long nanos, String... tags) {
        histogram(metric, nanos / 1_000_000_000., new Tags().tags(tags));
    }

    default void nanos(String metric, long nanos, Tags tags) {
        histogram(metric, nanos / 1_000_000_000., tags);
    }

    /**
     * @param metric timer name
     * @return new timer
     */
    default Timer timer(String metric) {
        return new Timer(this, metric);
    }


    /**
     * Records a value for the histogram
     *
     * @param metric histogram name
     * @param value histogram value
     * @param tags additional tags
     */
    default void histogram(String metric, double value, String... tags) {
        histogram(metric, value, new Tags().tags(tags));
    }

    void histogram(String metric, double value, Tags tags);

    /**
     * Records a value for the histogram
     *
     * @param metric histogram name
     * @param value histogram value
     * @param tags additional tags
     */
    default void histogram(String metric, long value, String... tags) {
        histogram(metric, value, new Tags().tags(tags));
    }

    void histogram(String metric, long value, Tags tags);

    /**
     * @param metric histogram name
     * @return new histogram
     */
    default Histogram histogram(String metric) {
        return new Histogram(this, metric);
    }


    /**
     * Records a value for the set
     *
     * @param metric metric name
     * @param value  value
     * @param tags   additional tags
     */
    default void set(String metric, String value, String... tags) {
        set(metric, value, new Tags().tags());
    }

    void set(String metric, String value, Tags tags);

    /**
     * Records a value for the specified named set0.
     *
     * @param metric metric name
     * @param value value
     * @param tags additional tags
     */
    default void set(String metric, long value, String... tags) {
        set(metric, value, new Tags().tags());
    }

    void set(String metric, long value, Tags tags);

    /**
     * @param metric set name
     * @return new histogram
     */
    default Set set(String metric) {
        return new Set(this, metric);
    }


    /**
     * @param event event to send
     */
    void event(Event event);

    /**
     * @param title event title
     * @param text event text
     * @return new event
     */
    default Event event(String title, String text) {
        return new Event(this, title, text);
    }


    /**
     * Send service check
     */
    void serviceCheck(ServiceCheck check);

    /**
     * @param name service check name
     * @param status service check status
     * @return new service check
     */
    default ServiceCheck serviceCheck(String name, Status status) {
        return new ServiceCheck(this, name, status);
    }
}
