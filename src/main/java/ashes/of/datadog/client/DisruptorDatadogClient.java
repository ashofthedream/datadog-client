package ashes.of.datadog.client;

import ashes.of.datadog.client.metrics.Event;
import ashes.of.datadog.client.metrics.MetricType;
import ashes.of.datadog.client.utils.BufferFormatter;
import ashes.of.datadog.client.metrics.ServiceCheck;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;


public class DisruptorDatadogClient extends AbstractDatadogClient {

    private final DatagramChannel channel;
    private final Disruptor<ByteBuf> disruptor;
    private final RingBuffer<ByteBuf> buffer;


    /**
     * @param b builder
     */
    public DisruptorDatadogClient(DatadogBuilder b) {
        super(b);
        this.channel = b.newChannel();
        this.disruptor = new Disruptor<>(newEventFactory(b.isDirectMemoryUsed(), b.getBufferSize()),
                b.getQueueSize(), b.getThreadFactory());

        this.disruptor.handleEventsWith(this::handleEvent);
        this.disruptor.setDefaultExceptionHandler(new ExceptionHandler<ByteBuf>() {
            @Override
            public void handleEventException(Throwable ex, long sequence, ByteBuf event) {
                errorHandler.accept(ex);
            }

            @Override
            public void handleOnStartException(Throwable ex) {
                errorHandler.accept(ex);
            }

            @Override
            public void handleOnShutdownException(Throwable ex) {
                errorHandler.accept(ex);
            }
        });

        this.buffer = disruptor.start();
    }

    private static EventFactory<ByteBuf> newEventFactory(boolean directMemoryUsed, int bufferSize) {
        return () -> directMemoryUsed ?
                Unpooled.directBuffer(bufferSize) :
                Unpooled.buffer(bufferSize);
    }


    private void handleEvent(ByteBuf buf, long seq, boolean eob) throws Exception {
        ByteBuffer nio = buf.nioBuffer();

        int remaining = nio.remaining();
        int sent = channel.write(nio);

        if (remaining != sent)
            throw new IOException(String.format("Sent only %d bytes of %d bytes", sent, remaining));
    }

    @Override
    public void stop() {
        disruptor.halt();
    }


    @Override
    protected void send(String metric, long value, MetricType type, Tags tags) {
        buffer.publishEvent((b, seq) -> writeMetric(b, prefix, metric, type, tags, buf -> BufferFormatter.append(b, value) ));
    }

    @Override
    protected void send(String metric, double value, MetricType type, Tags tags) {
        buffer.publishEvent((b, seq) -> writeMetric(b, prefix, metric, type, tags, buf -> BufferFormatter.append(buf, value, 6, false) ));
    }

    @Override
    protected void send(String metric, String value, MetricType type, Tags tags) {
        buffer.publishEvent((b, seq) -> writeMetric(b, prefix, metric, type, tags, buf -> BufferFormatter.append(b, value) ));
    }

    private void writeMetric(ByteBuf b, @Nullable String prefix, String metric, MetricType type, Tags tags, Consumer<ByteBuf> valueWriter) {
        b.clear();

        if (prefix != null) {
            b.writeCharSequence(prefix, UTF_8);
            b.writeByte('.');
        }
        b.writeCharSequence(metric, UTF_8);
        b.writeByte(':');

        valueWriter.accept(b);

        b.writeByte('|');
        b.writeByte(type.getType());


        writeTags(b, global, tags);
    }

    private void writeTags(ByteBuf b, Tags global, Tags metric) {
        boolean hasGlobalTags = !global.isEmpty();
        boolean hasAdditionalTags = !metric.isEmpty();
        if (hasGlobalTags || hasAdditionalTags) {
            b.writeByte('|');
            b.writeByte('#');
        }

        writeTags(b, global);

        if (!hasAdditionalTags)
            return;

        if (hasGlobalTags)
            b.writeByte(',');

        writeTags(b, metric);
    }

    private void writeTags(ByteBuf b, Tags tags) {
        List<Supplier<String>> list = tags.list();
        for (int i = 0; i < list.size(); i++) {
            String tag = list.get(i).get();

            b.writeCharSequence(tag, UTF_8);
            if (i < list.size() - 1)
                b.writeByte(',');
        }
    }


    /**
     * @param event event
     */
    @Override
    public void event(Event event) {
        buffer.publishEvent((b, seq) -> writeEvent(b, event));
    }


    /**
     * _e{title.length,text.length}:title|text|d:date_happened|h:hostname|p:priority|t:alert_type|#tag1,tag2
     *
     * @param b buffer
     * @param event event to write
     */
    private void writeEvent(ByteBuf b, Event event) {
        String title = event.getTitle();
        String text = event.getText();

        b.clear();
        b.writeCharSequence("_e{", UTF_8);
        BufferFormatter.append(b, title.length());
        b.writeByte(',');
        BufferFormatter.append(b, text.length());
        b.writeByte('}');
        b.writeByte(':');
        b.writeCharSequence(title, UTF_8);
        b.writeByte('|');
        b.writeCharSequence(text, UTF_8);

        if (event.getTime() > 0) {
            b.writeCharSequence("|d:", UTF_8);
            BufferFormatter.append(b, event.getTime() / 1000);
        }

        String hostname = event.getHostname();
        if (hostname != null) {
            b.writeCharSequence("|h:", UTF_8);
            b.writeCharSequence(hostname, UTF_8);
        }

        String aggregationKey = event.getAggregationKey();
        if (aggregationKey != null) {
            b.writeCharSequence("|k:", UTF_8);
            b.writeCharSequence(aggregationKey, UTF_8);
        }

        Event.Priority priority = event.getPriority();
        if (priority != null) {
            b.writeCharSequence("|p:", UTF_8);
            b.writeCharSequence(priority.name().toLowerCase(), UTF_8);
        }

        String source = event.getSourceType();
        if (source != null) {
            b.writeCharSequence("|s:", UTF_8);
            b.writeCharSequence(source, UTF_8);
        }

        Event.AlertType alert = event.getAlertType();
        if (alert != null) {
            b.writeCharSequence("|t:", UTF_8);
            b.writeCharSequence(alert.name().toLowerCase(), UTF_8);
        }

        writeTags(b, global, event.tags());
    }


    /**
     * @param check service check
     */
    @Override
    public void serviceCheck(ServiceCheck check) {
        buffer.publishEvent((b, seq) -> writeServiceCheck(b, check));
    }

    /**
     * _sc|name|status|d:timestamp|h:hostname|#tag1,tag2|m:service_check_message
     *
     * @param b buffer
     * @param check service check to write
     */
    private void writeServiceCheck(ByteBuf b, ServiceCheck check) {
        b.clear();
        b.writeCharSequence("_sc", UTF_8);
        b.writeByte('|');
        b.writeCharSequence(check.getName(), UTF_8);
        b.writeByte('|');
        BufferFormatter.append(b, check.getStatus().ordinal());

        if (check.getTime() > 0) {
            b.writeCharSequence("|d:", UTF_8);
            BufferFormatter.append(b, check.getTime() / 1000);
        }

        String hostname = check.getHostname();
        if (hostname != null) {
            b.writeCharSequence("|h:", UTF_8);
            b.writeCharSequence(hostname, UTF_8);
        }

        writeTags(b, global, check.tags());

        String message = check.getMessage();
        if (message != null) {
            b.writeCharSequence("|m:", UTF_8);
            b.writeCharSequence(message, UTF_8);
        }
    }
}


