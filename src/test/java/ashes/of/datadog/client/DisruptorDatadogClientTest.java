package ashes.of.datadog.client;

import ashes.of.datadog.client.metrics.*;
import ashes.of.datadog.client.metrics.Event.AlertType;
import ashes.of.datadog.client.metrics.Event.Priority;
import ashes.of.datadog.server.DatadogServer;
import org.junit.*;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class DisruptorDatadogClientTest {

    private static final InetSocketAddress address = new InetSocketAddress("localhost", 31337);

    private DatadogServer server;
    private DatadogClient withPrefix, withPrefixAndTags, noPrefixAndTags;

    private String dynamicTag = "";
    
    @Before
    public void setUp() throws Exception {
        server = new DatadogServer(address);
        server.start();

        dynamicTag = "nope";
        
        withPrefix = new DatadogBuilder()
                .address(address)
                .prefix("test")
                .build(DisruptorDatadogClient::new);

        withPrefixAndTags = new DatadogBuilder()
                .address(address)
                .prefix("test")
                .tag("env", "junit")
                .tag("dynamic", () -> dynamicTag)
                .build(DisruptorDatadogClient::new);

        noPrefixAndTags = new DatadogBuilder()
                .address(address)
                .build(DisruptorDatadogClient::new);
    }

    @After
    public void shutDown() {
        withPrefix.stop();
        withPrefixAndTags.stop();
        noPrefixAndTags.stop();
        server.stop();
    }


    /**
     * @see DatadogClient#count(String, long, String...)
     * @see DatadogClient#increment(String, String...)
     * @see DatadogClient#decrement(String, String...)
     */
    @Test
    public void countShouldSendMetricWithoutPrefixAndTags() {
        noPrefixAndTags.count("count", 24);

        assertEquals("count:24|c", server.poll());
    }

    @Test
    public void countShouldSendMetricWithValue() {
        withPrefixAndTags.count("count", 42);

        assertEquals("test.count:42|c|#env:junit,dynamic:nope", server.poll());
    }

    @Test
    public void countShouldSendMetricWithNegativeValueAngTags() {
        withPrefix.count("count", -42, "negative");

        assertEquals("test.count:-42|c|#negative", server.poll());
    }

    @Test
    public void incrementShouldSendMetric() {
        withPrefix.increment("inc");

        assertEquals("test.inc:1|c", server.poll());
    }

    @Test
    public void decrementShouldSendMetric() {
        noPrefixAndTags.decrement("dec");

        assertEquals("dec:-1|c", server.poll());
    }


    /**
     * @see DatadogClient#gauge(String, long, String...)
     * @see DatadogClient#gauge(String, double, String...)
     */
    @Test
    public void gaugeShouldSendMetricWithLongValue() {
        withPrefix.gauge("gauge", 1337);

        assertEquals("test.gauge:1337|g", server.poll());
    }

    @Test
    public void gaugeShouldSendMetricWithDoubleValue() {
        withPrefix.gauge("gauge", 123.45678901234567890);

        assertEquals("test.gauge:123.456789|g", server.poll());
    }

    @Test
    public void gaugeShouldSendMetricWithVeryLargeDoubleValue() {
        noPrefixAndTags.gauge("gauge", 123456789012345.67890);

        assertThat(server.poll(), matchesPattern("gauge:123456789012345.6.....\\|g"));
    }

    @Test
    public void gaugeShouldSendMetricWithSmallDoubleValue() {
        dynamicTag = "yep";
        withPrefixAndTags.gauge("gauge", 0.001337);

        assertEquals("test.gauge:0.001337|g|#env:junit,dynamic:yep", server.poll());
    }


    /**
     * @see DatadogClient#histogram(String, long, String...)
     * @see DatadogClient#histogram(String, double, String...)
     * @see DatadogClient#histogram(String)
     */
    @Test
    public void histogramShouldSendMetricWithLongValueAndTags() {
        withPrefix.histogram("histogram", 423, "foo:bar", "baz");

        assertEquals("test.histogram:423|h|#foo:bar,baz", server.poll());
    }

    @Test
    public void histogramShouldSendMetricWithDoubleValueAndNoTags() {
        withPrefix.histogram("histogram", 0.423);

        assertEquals("test.histogram:0.423000|h", server.poll());
    }


    /**
     * @see DatadogClient#millis(String, long, String...)
     * @see DatadogClient#nanos(String, long, String...)
     * @see DatadogClient#timer(String)
     */
    @Test
    public void millisShouldSendTimeAsHistogramDoubleValue() {
        withPrefix.millis("time", 123);

        assertEquals("test.time:0.123000|h", server.poll());
    }

    @Test
    public void nanosShouldSendTimeAsHistogramDoubleValue() {
        noPrefixAndTags.nanos("time", 456000);

        assertEquals("time:0.000456|h", server.poll());
    }


    /**
     * @see DatadogClient#event(String, String)
     * @see DatadogClient#send(Event)
     */
    @Test
    public void eventSendShouldSendEventWithTitleAndText() {
        noPrefixAndTags.event("Hello", "This is base event for test")
                .send();

        assertEquals("_e{5,27}:Hello|This is base event for test", server.poll());
    }

    @Test
    public void eventShouldSendEventWithAllData() {
        noPrefixAndTags.event("Holy Shit", "This is huge error with low priority")
                .alertType(AlertType.ERROR)
                .time(1514764800000L)
                .priority(Priority.LOW)
                .tag("huge", "fact")
                .aggregationKey("HUGE_ERROR")
                .sourceType("the_source_test")
                .send();


        assertEquals("_e{9,36}:Holy Shit|This is huge error with low priority|d:1514764800|k:HUGE_ERROR|p:low|s:the_source_test|t:error|#huge:fact", server.poll());
    }


    /**
     * @see DatadogClient#serviceCheck(String, ServiceCheck.Status)
     * @see DatadogClient#send(ServiceCheck)
     */
    @Test
    public void serviceCheckShouldSendCheckWithTitleAndStatus() {
        noPrefixAndTags.serviceCheck("Holy Shit", ServiceCheck.Status.OK)
                .send();

        assertEquals("_sc|Holy Shit|0", server.poll());
    }


    @Test
    public void serviceCheckShouldSendCheckWithAllData() {
        noPrefixAndTags.serviceCheck("Holy Shit", ServiceCheck.Status.CRITICAL)
                .message("Oh shit... all goes pussy")
                .hostname("ashesofmbr")
                .tag("howareyou", "verygood")
                .send();

        assertEquals("_sc|Holy Shit|2|h:ashesofmbr|#howareyou:verygood|m:Oh shit... all goes pussy", server.poll());
    }
}
