package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


public class Timer extends Metric<Timer> {

    private long start = System.nanoTime();

    public Timer(DatadogClient client, String name) {
        super(client, name);
    }

    /**
     * Restarts the timer
     */
    public void restart() {
        start = System.nanoTime();
    }

    /**
     * Records elapsed time in milliseconds
     *
     * @param elapsed time
     */
    public void millis(long elapsed) {
        elapsed(elapsed, TimeUnit.MILLISECONDS);
    }

    /**
     * Records elapsed time in nanoseconds
     *
     * @param elapsed time
     */
    public void nanos(long elapsed) {
        elapsed(elapsed, TimeUnit.NANOSECONDS);
    }


    /**
     * Records elapsed time from the last timers restart
     */
    public void elapsed() {
        nanos(System.nanoTime() - start);
    }

    /**
     * Records elapsed time in specified time unit
     *
     * @param time time
     * @param unit time unit
     */
    public void elapsed(long time, TimeUnit unit) {
        client.nanos(name, unit.toNanos(time), tags());
    }

    /**
     * @see this#elapsed(long, TimeUnit)
     */
    public void elapsed(Duration duration) {
        client.nanos(name, duration.toNanos(), tags());
    }


    public void measure(Runnable runnable) {
        restart();
        runnable.run();
        elapsed();
    }
}
