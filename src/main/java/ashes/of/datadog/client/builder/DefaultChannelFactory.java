package ashes.of.datadog.client.builder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import static java.net.StandardSocketOptions.SO_SNDBUF;

public class DefaultChannelFactory implements ChannelFactory {

    @Override
    public DatagramChannel createChannel(InetSocketAddress address) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.setOption(SO_SNDBUF, 1 << 16);
        channel.connect(address);

        return channel;
    }
}
