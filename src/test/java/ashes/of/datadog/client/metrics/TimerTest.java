package ashes.of.datadog.client.metrics;

import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class TimerTest extends MetricsTest {

    @Test
    public void millisShouldSendTimeInSeconds() {
        Timer timer = withPrefixAndTags.timer("time")
                .tag("method", "millis");

        timer.millis(123_456);
        assertEquals("test.time:123.456000|h|#env:junit,method:millis", server.poll());
    }

    @Test
    public void nanosShouldSendTimeInSeconds() {
        Timer timer = noPrefixAndTags.timer("time")
                .tag("method", "nanos");

        timer.nanos(123_456_000_000L);

        assertEquals("time:123.456000|h|#method:nanos", server.poll());
    }



    @Test
    public void elapsedShouldSendTimeInSeconds() {
        Timer timer = withPrefixAndTags.timer("time")
                .tag("method", "elapsed");

        timer.elapsed(Duration.ofMillis(123_456));

        assertEquals("test.time:123.456000|h|#env:junit,method:elapsed", server.poll());
    }
}
