package ashes.of.datadog.client.metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GaugeTest extends MetricsTest {

    @Test
    public void valueShouldSendLongMetricWithSameTags() {
        Gauge gauge = withPrefixAndTags.gauge("gauge");

        gauge.value(5);
        gauge.value(6);
        gauge.value(7);

        assertEquals("test.gauge:5|g|#env:junit", server.poll());
        assertEquals("test.gauge:6|g|#env:junit", server.poll());
        assertEquals("test.gauge:7|g|#env:junit", server.poll());
    }

    @Test
    public void valueShouldSendDoubleMetricWithSameTags() {
        Gauge gauge = withPrefixAndTags.gauge("gauge");

        gauge.value(5.4);
        gauge.value(5.7);

        assertEquals("test.gauge:5.400000|g|#env:junit", server.poll());
        assertEquals("test.gauge:5.700000|g|#env:junit", server.poll());
    }
}
