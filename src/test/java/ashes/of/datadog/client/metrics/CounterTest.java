package ashes.of.datadog.client.metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CounterTest extends MetricsTest {

    @Test
    public void incShouldSendMetricWithSameTags() {
        Counter counter = withPrefixAndTags.counter("inc")
                .tag("test", "incWithTags");

        counter.inc();
        counter.inc();
        counter.inc();

        assertEquals("test.inc:1|c|#env:junit,test:incWithTags", server.poll());
        assertEquals("test.inc:1|c|#env:junit,test:incWithTags", server.poll());
        assertEquals("test.inc:1|c|#env:junit,test:incWithTags", server.poll());
    }

    @Test
    public void decShouldSendMetricWithSameTags() {
        Counter counter = noPrefixAndTags.counter("dec");

        counter.dec();
        counter.dec();
        counter.dec();

        assertEquals("dec:-1|c", server.poll());
        assertEquals("dec:-1|c", server.poll());
        assertEquals("dec:-1|c", server.poll());
    }

    @Test
    public void countShouldSendMetricWithSameTags() {
        Counter counter = withPrefixAndTags.counter("counter")
                .tag("test", "countShould");

        counter.count(-5);
        counter.count(-3);
        counter.count(4);

        assertEquals("test.counter:-5|c|#env:junit,test:countShould", server.poll());
        assertEquals("test.counter:-3|c|#env:junit,test:countShould", server.poll());
        assertEquals("test.counter:4|c|#env:junit,test:countShould", server.poll());
    }
}
