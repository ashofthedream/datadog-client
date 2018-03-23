package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;

import javax.annotation.Nullable;
import java.time.Instant;


public class Event extends Metric<Event> {

    public enum Priority {
        LOW,
        NORMAL
    }

    public enum AlertType {
        ERROR,
        WARNING,
        INFO,
        SUCCESS
    }


    private final String text;

    private long time;

    @Nullable
    private String hostname;

    @Nullable
    private String aggregationKey;

    @Nullable
    private String sourceType;

    @Nullable
    private Priority priority;

    @Nullable
    private AlertType alertType;


    public Event(DatadogClient client, String title, String text) {
        super(client, title);
        this.text = text;
    }


    public String getTitle() {
        return name;
    }

    public String getText() {
        return text;
    }


    /**
     * Assign a time to the event; Default: none
     *
     * @param time event time
     */
    public Event time(long time) {
        this.time = time;
        return this;
    }

    /**
     * @see this#time(long)
     */
    public Event time(Instant time) {
        return time(time.toEpochMilli());
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
    public Event hostname(@Nullable String hostname) {
        this.hostname = hostname;
        return this;
    }

    @Nullable
    public String getHostname() {
        return hostname;
    }


    /**
     * Assign an aggregation key to the event, to group it with some others; Default: none
     *
     * @param key aggregation key
     */
    public Event aggregationKey(String key) {
        this.aggregationKey = key;
        return this;
    }

    @Nullable
    public String getAggregationKey() {
        return aggregationKey;
    }


    /**
     * @param priority Can be "normal" or "low"
     */
    public Event priority(Priority priority) {
        this.priority = priority;
        return this;
    }

    @Nullable
    public Priority getPriority() {
        return priority;
    }


    /**
     * Assign a source type to the event ; Default: none
     *
     * @param source event source
     */
    public Event sourceType(String source) {
        this.sourceType = source;
        return this;
    }

    @Nullable
    public String getSourceType() {
        return sourceType;
    }


    /**
     * Assign a type for event, can be "error", "warning", "info" or "success"; Default: "info"
     *
     * @param alert alert type
     */
    public Event alertType(AlertType alert) {
        this.alertType = alert;
        return this;
    }

    @Nullable
    public AlertType getAlertType() {
        return alertType;
    }


    public void send() {
        client.send(this);
    }
}
