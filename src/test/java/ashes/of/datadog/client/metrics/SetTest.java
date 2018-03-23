package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class SetTest extends MetricsTest {


    /**
     * @see DatadogClient#set(String, String, String...)
     * @see DatadogClient#set(String, long, String...)
     * @see DatadogClient#set(String)
     */
    @Test
    public void setShouldSendMetricWithTags() {
        AtomicReference<String> statusSubtag = new AtomicReference<>("denied");

        Set set = withPrefixAndTags.set("set")
                .tag("status", statusSubtag::get)
                .tag("access", "user");

        set.add(123);

        statusSubtag.set("restricted");
        set.add(124);

        assertEquals("test.set:123|s|#env:junit,status:denied,access:user", server.poll());
        assertEquals("test.set:124|s|#env:junit,status:restricted,access:user", server.poll());
    }
}
