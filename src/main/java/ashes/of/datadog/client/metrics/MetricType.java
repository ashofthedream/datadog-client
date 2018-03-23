package ashes.of.datadog.client.metrics;

/**
 * Metric types
 */
public enum MetricType {
    COUNTER('c'),
    GAUGE('g'),
    HISTOGRAM('h'),
    SET('s');

    private final char type;

    MetricType(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }
}
