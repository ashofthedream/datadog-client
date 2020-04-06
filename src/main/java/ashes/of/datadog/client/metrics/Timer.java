package ashes.of.datadog.client.metrics;

import ashes.of.datadog.client.DatadogClient;
import ashes.of.datadog.client.function.CheckedRunnable;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.*;


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
        client.nanos(name, unit.toNanos(time), tags);
    }

    /**
     * @see this#elapsed(long, TimeUnit)
     */
    public void elapsed(Duration duration) {
        client.nanos(name, duration.toNanos(), tags);
    }


    public void measure(Runnable runnable) {
        long start = System.nanoTime();
        try {
            runnable.run();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }

    public Runnable wrap(Runnable runnable) {
        return () -> measure(runnable);
    }


    public void measureChecked(CheckedRunnable runnable) throws Exception {
        long start = System.nanoTime();
        try {
            runnable.run();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }

    public CheckedRunnable wrapChecked(CheckedRunnable runnable) {
        return () -> measureChecked(runnable);
    }


    public <T> void measure(Consumer<T> c, T a) {
        measure(() -> c.accept(a));
    }

    public <T> Consumer<T> wrap(Consumer<T> c) {
        return a -> measure(c, a);
    }


    public void measure(IntConsumer c, int a) {
        measure(() -> c.accept(a));
    }

    public IntConsumer wrap(IntConsumer c) {
        return a -> measure(c, a);
    }


    public void measure(LongConsumer c, long a) {
        measure(() -> c.accept(a));
    }

    public LongConsumer wrap(LongConsumer c) {
        return a -> measure(c, a);
    }


    public void measure(DoubleConsumer c, double a) {
        measure(() -> c.accept(a));
    }

    public DoubleConsumer wrap(DoubleConsumer c) {
        return a -> measure(c, a);
    }



    public <A, B> void measure(BiConsumer<A, B> c, A a, B b) {
        measure(() -> c.accept(a, b));
    }

    public <A, B> BiConsumer<A, B> wrap(BiConsumer<A, B> c) {
        return (a, b) -> measure(c, a, b);
    }


    public <A, R> R measure(Function<A, R> f, A a) {
        return measure(() -> f.apply(a));
    }

    public <A, R> Function<A, R> wrap(Function<A, R> f) {
        return a -> measure(f, a);
    }


    public <A, B, R> R measure(BiFunction<A, B, R> f, A a, B b) {
        return measure(() -> f.apply(a, b));
    }

    public <A, B, R> BiFunction<A, B, R> wrap(BiFunction<A, B, R> f) {
        return (a, b) -> measure(f, a, b);
    }



    public <T> T measure(Supplier<T> s) {
        long start = System.nanoTime();
        try {
            return s.get();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }

    public <T> Supplier<T> wrap(Supplier<T> s) {
        return () -> measure(s);
    }


    public <T> T measureChecked(Callable<T> c) throws Exception {
        long start = System.nanoTime();
        try {
            return c.call();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }


    public int measure(IntSupplier s) {
        long start = System.nanoTime();
        try {
            return s.getAsInt();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }

    public IntSupplier wrap(IntSupplier s) {
        return () -> measure(s);
    }


    public long measure(LongSupplier s) {
        long start = System.nanoTime();
        try {
            return s.getAsLong();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }

    public LongSupplier wrap(LongSupplier s) {
        return () -> measure(s);
    }


    public double measure(DoubleSupplier s) {
        long start = System.nanoTime();
        try {
            return s.getAsDouble();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }

    public DoubleSupplier wrap(DoubleSupplier s) {
        return () -> measure(s);
    }


    public boolean measure(BooleanSupplier s) {
        long start = System.nanoTime();
        try {
            return s.getAsBoolean();
        } finally {
            nanos(System.nanoTime() - start);
        }
    }

    public BooleanSupplier wrap(BooleanSupplier s) {
        return () -> measure(s);
    }

}
