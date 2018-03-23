package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;


/**
 * Histograms measure the statistical distribution of a set of values.
 *
 * It aggregates the values that are sent during the flush interval (usually defaults to 10 seconds).
 * So if you send 20 values for a metric during the flush interval, it’ll give you the aggregation of those values
 * for the flush interval, i.e.:
 *
 * - my_metric.avg:             avg of those 20 values during the flush interval
 * - my_metric.count:           count of the values sent during the flush interval
 * - my_metric.median:          median of those values in the flush interval
 * - my_metric.95percentile:    95th percentile value in the flush interval
 * - my_metric.max:             max value sent during the flush interval
 * - my_metric.min:             min value sent during the flush interval
 */
public class Histogram extends Metric<Histogram> {

    /**
     * @param client datadog client
     * @param name metric name
     */
    public Histogram(DatadogClient client, String name) {
        super(client, name);
    }

    /**
     * Records a value for the histogram
     *
     * @param value histogram value
     */
    public void value(long value) {
        client.histogram(name, value, tags());
    }

    /**
     * Records a value for the histogram
     *
     * @param value histogram value
     */
    public void value(double value) {
        client.histogram(name, value, tags());
    }
}
