package ashes.of.datadog.client.metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HistogramTest extends MetricsTest {

    @Test
    public void valueShouldSendLongMetricWithSameTags() {
        Histogram histo = withPrefixAndTags.histogram("histogram")
                .tag("multiply", "yes");

        histo.value(1337);
        histo.value(1338);
        histo.value(1334);

        assertEquals("test.histogram:1337|h|#env:junit,multiply:yes", server.poll());
        assertEquals("test.histogram:1338|h|#env:junit,multiply:yes", server.poll());
        assertEquals("test.histogram:1334|h|#env:junit,multiply:yes", server.poll());
    }

    @Test
    public void valueShouldSendDoubleMetricWithSameTags() {
        Histogram histo = withPrefixAndTags.histogram("histogram")
                .tag("multiply", "yes");

        histo.value(1337.5);
        histo.value(1336.7);

        assertEquals("test.histogram:1337.500000|h|#env:junit,multiply:yes", server.poll());
        assertEquals("test.histogram:1336.700000|h|#env:junit,multiply:yes", server.poll());
    }
}
