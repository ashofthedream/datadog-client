Maven dependency:
```xml
<dependency>
    <groupId>ashes.of</groupId>
    <artifactId>datadog-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

Example:
```java
import ashes.of.datadog.client.metrics.*;
import ashes.of.datadog.client.metrics.Event.AlertType;
import ashes.of.datadog.client.metrics.Event.Priority;

public class Example {

    public static final String STATUS = "OK";

    public static void main(String... args) {
        DatadogClient client = new DatadogBuilder()
                .address("localhost", 8125)
                .prefix("test.prefix")                  // Prefix to any stats (optional)
                .tag("test")                            // tags that always applied for each metric
                .tag("foo", "bar")
                .tag("status", () -> STATUS)
                .build();

        // Counter metric
        client.increment("errors");
        client.decrement("errors");
        client.count("errors", 1337, "huge"); // counter event with simple tag

        // Gauge metrics
        client.gauge("threads_count", 100);
        client.gauge("size_of_something", 0.01);

        Gauge gauge = client.gauge("size_of_something");
        gauge.value(0.02);
        gauge.value(0.06);

        // Histograms
        client.histogram("histo", 15);
        client.histogram("histo", 15.5);

        Histogram histo = client.histogram("histo");
        histo.value(16);
        histo.value(17.2);


        // Timers
        client.millis("request_time", 25, "request:getUser");
        client.nanos("request_time", 1250, "HashMap:put");

        Timer timer = client.timer("method_time")
                .tag("method", "someHugeMethod");

        // In this point we can invoke some method to measure it  
        someHugeMethod();

        timer.elapsed();

        // Or we can 
        timer.measure(Example::someHugeMethod);


        // Events
        Event event = client.event("Hello", "This is base event for test")
                .alertType(AlertType.INFO)
                .priority(Priority.LOW)
                .aggregationKey("HUGE_ERROR")
                .tag("color", "green");

        event.send();

        // Service checks
        ServiceCheck check = client.serviceCheck("I'm okay", ServiceCheck.Status.OK)
                .message("some additional message")
                .tag("color", "green");

        check.send();
    }



    private static void someHugeMethod() {
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

