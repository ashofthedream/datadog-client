package ashes.of.datadog.server;

import ashes.of.datadog.client.utils.DefaultThreadFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


public class DatadogServer {

    private static final ThreadFactory threadFactory = new DefaultThreadFactory("datadog-server-%d", true);

    private final DatagramSocket socket;
    private final BlockingQueue<String> queue;
    private final Thread thread;


    public DatadogServer(InetSocketAddress address) {
        this(address, new ArrayBlockingQueue<>(1024));
    }

    public DatadogServer(InetSocketAddress address, BlockingQueue<String> queue) {
        try {
            this.socket = new DatagramSocket(address);
        } catch (SocketException e) {
            throw new UncheckedIOException(e);
        }
        this.queue = queue;
        this.thread = threadFactory.newThread(this::receivePackets);
    }


    private void receivePackets() {
        while (!socket.isClosed())
            receivePacket();
    }

    private void receivePacket() {
        try {
            byte[] buf = new byte[1536];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            socket.receive(packet);

            queue.add(new String(packet.getData(), packet.getOffset(), packet.getLength(), Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    public String poll() {
        return poll(1000);
    }

    @Nullable
    public String poll(long ms) {
        try {
            return queue.poll(ms, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> pollAll() {
        List<String> list = new ArrayList<>();
        queue.drainTo(list);
        return list;
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        socket.close();
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
