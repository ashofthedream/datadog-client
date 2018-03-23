package ashes.of.datadog.client;

import ashes.of.datadog.client.builder.ChannelFactory;
import ashes.of.datadog.client.builder.DefaultChannelFactory;
import io.netty.util.concurrent.DefaultThreadFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class DatadogBuilder implements Taggable<DatadogBuilder> {

    protected Tags tags = new Tags();

    @Nullable
    private String prefix;

    /**
     * Byte buffer buffer size
     */
    private int bufferSize = 1280;

    /**
     * Metrics queue size
     */
    private int queueSize = 1 << 16;

    /**
     *
     */
    private boolean useDirectMemory;

    /**
     * Agent address, by default agent runs on localhost:8125
     */
    private InetSocketAddress address = new InetSocketAddress("localhost", 8125);
    private ThreadFactory threadFactory = new DefaultThreadFactory("datadog-client-%d", true);
    private ChannelFactory channelFactory = new DefaultChannelFactory();
    private Consumer<Throwable> errorHandler = ex -> {};



    @Nullable
    protected String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix for all metrics
     * If prefix is null, client will be created without prefix
     *
     * @param prefix prefix string for all metrics
     * @return builder
     */
    public DatadogBuilder prefix(@Nullable String prefix) {
        this.prefix = prefix;
        return this;
    }


    public int getBufferSize() {
        return bufferSize;
    }

    public DatadogBuilder bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }


    public DatadogBuilder queueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    public int getQueueSize() {
        return queueSize;
    }


    public boolean isDirectMemoryUsed() {
        return useDirectMemory;
    }

    public DatadogBuilder useDirectMemory() {
        this.useDirectMemory = true;
        return this;
    }

    /**
     * @see this#address(InetSocketAddress)
     */
    public DatadogBuilder address(String host, int port) {
        return address(new InetSocketAddress(host, port));
    }

    /**
     * Sets the address for datadogs agent
     * @return builder
     */
    public DatadogBuilder address(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public InetSocketAddress getAddress() {
        return address;
    }


    /**
     * @return new datagram channel
     */
    public DatagramChannel newChannel() {
        try {
            return channelFactory.createChannel(address);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ChannelFactory getChannelFactory() {
        return channelFactory;
    }

    public DatadogBuilder channelFactory(ChannelFactory channelFactory) {
        Objects.requireNonNull(channelFactory, "Channel factory is null");
        this.channelFactory = channelFactory;
        return this;
    }



    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public DatadogBuilder threadFactory(ThreadFactory threadFactory) {
        Objects.requireNonNull(threadFactory, "ThreadFactory is null");
        this.threadFactory = threadFactory;
        return this;
    }




    public DatadogBuilder errorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler can't be null");
        return this;
    }

    public Consumer<Throwable> getErrorHandler() {
        return errorHandler;
    }



    @Override
    public Stream<String> stream() {
        return tags.stream();
    }

    @Override
    public DatadogBuilder tag(String tag, Supplier<Object> sub) {
        tags.tag(tag, sub);
        return this;
    }


    public Tags getTags() {
        return tags;
    }


    /**
     * @return build datadog client via function
     */
    public DatadogClient build(Function<DatadogBuilder, DatadogClient> f) {
        return f.apply(this);
    }

    /**
     * @return build {@link DisruptorDatadogClient} and returns it
     */
    public DatadogClient build() {
        return build(DisruptorDatadogClient::new);
    }

    /**
     * @return build {@link MockDatadogClient} and returns it
     */
    public DatadogClient mock() {
        return build(MockDatadogClient::new);
    }
}
