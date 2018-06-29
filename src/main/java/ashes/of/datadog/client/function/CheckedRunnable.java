package ashes.of.datadog.client.function;


@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
