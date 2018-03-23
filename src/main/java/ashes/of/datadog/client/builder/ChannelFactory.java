package ashes.of.datadog.client.builder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

@FunctionalInterface
public interface ChannelFactory {
    DatagramChannel createChannel(InetSocketAddress address) throws IOException;
}
