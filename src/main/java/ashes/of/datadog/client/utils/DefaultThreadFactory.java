package ashes.of.datadog.client.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private final AtomicInteger threadId = new AtomicInteger();
    private final String namePattern;
    private final boolean daemon;

    public DefaultThreadFactory(String namePattern, boolean daemon) {
        this.namePattern = namePattern;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(String.format(namePattern, threadId.getAndIncrement()));
        thread.setDaemon(daemon);

        return thread;
    }
}
