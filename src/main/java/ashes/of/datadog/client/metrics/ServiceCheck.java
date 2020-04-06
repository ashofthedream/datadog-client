package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;

import javax.annotation.Nullable;
import java.time.Instant;

public class ServiceCheck extends Metric<ServiceCheck> {

    public enum Status {
        OK,
        WARNING,
        CRITICAL,
        UNKNOWN
    }


    private final Status status;

    private long time;

    @Nullable
    private String hostname;

    /**
     * A message describing the current state of the service check.
     */
    @Nullable
    private String message;


    public ServiceCheck(DatadogClient client, String name, Status status) {
        super(client, name);
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }


    /**
     * Assign a timestamp to the event ; Default: none ()
     * Default is the current Unix epoch timestamp when not sent
     *
     * @param time
     */
    public ServiceCheck time(long time) {
        this.time = time;
        return this;
    }

    /**
     * @see this#time(long)
     */
    public ServiceCheck time(Instant instant) {
        return time(instant.toEpochMilli());
    }

    /**
     * @return 0 if not set
     */
    public long getTime() {
        return time;
    }


    /**
     * Assign a hostname to the event; Default: none
     *
     * @param hostname hostname
     */
    public ServiceCheck hostname(@Nullable String hostname) {
        this.hostname = hostname;
        return this;
    }

    @Nullable
    public String getHostname() {
        return hostname;
    }
    
    /**
     * Assign a message to the event; Default: none
     *
     * @param message message
     */
    public ServiceCheck message(@Nullable String message) {
        this.message = message;
        return this;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
    

    public void send() {
        client.serviceCheck(this);
    }
}

