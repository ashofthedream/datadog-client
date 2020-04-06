package ashes.of.datadog.client.benchmarks;


import ashes.of.datadog.client.DatadogBuilder;
import ashes.of.datadog.client.DatadogClient;
import ashes.of.datadog.client.DisruptorDatadogClient;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class DatadogClientBenchmark {

    @State(Scope.Thread)
    public static class DatadogClientState {

        protected DatadogClient client;
        protected InetSocketAddress address = new InetSocketAddress("localhost", 31337);

        @Setup
        public void setUp() throws Exception {
            client = new DatadogBuilder()
                    .address(address)
                    .prefix("ahahaha")
                    .tag("benchmark")
                    .tag("Lorem", "ipsum")
                    .tag("dolor_sit", "amet")
                    .tag("consectetur", "adipiscing")
                    .tag("current_time", System::currentTimeMillis)
                    .errorHandler(System.out::println)
                    .build(DisruptorDatadogClient::new);
        }

        @TearDown
        public void down() throws Exception {
            client.stop();
        }
    }

    @Benchmark
    public void histogramWithTwoTags(DatadogClientState state) throws Exception {
        state.client.histogram("hello.client")
                .tag("foo", "bar")
                .tag("the_foo", "the_bar")
                .value(1);
    }


    public static void main(String... args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(DatadogClientBenchmark.class.getSimpleName())
                .forks(1)
                .threads(1)
                .warmupTime(TimeValue.seconds(10))
                .warmupIterations(6)
                .measurementTime(TimeValue.seconds(10))
                .measurementIterations(6)
                .detectJvmArgs()
                .mode(Mode.SampleTime)
                .addProfiler(GCProfiler.class)
//                .mode(Mode.SingleShotTime)
                .shouldDoGC(true)
                .build();

        Runner runner = new Runner(opt);
        runner.run();
    }
}
