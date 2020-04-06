package ashes.of.datadog.client.metrics;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Duration;
import java.util.function.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class TimerTest extends MetricsTest {

    @Test
    public void millisShouldSendTimeInSeconds() {
        Timer timer = withPrefixAndTags.timer("time")
                .tag("method", "millis");

        timer.millis(123_456);
        assertEquals("test.time:123.456000|h|#env:junit,method:millis", server.poll());
    }

    @Test
    public void nanosShouldSendTimeInSeconds() {
        Timer timer = noPrefixAndTags.timer("time")
                .tag("method", "nanos");

        timer.nanos(123_456_000_000L);

        assertEquals("time:123.456000|h|#method:nanos", server.poll());
    }


    @Test
    public void elapsedShouldSendTimeInSeconds() {
        Timer timer = withPrefixAndTags.timer("time")
                .tag("method", "elapsed");

        timer.elapsed(Duration.ofMillis(123_456));

        assertEquals("test.time:123.456000|h|#env:junit,method:elapsed", server.poll());
    }


    /**
     * @see Timer#measure(Runnable)
     * @see Timer#wrap(Runnable)
     */
    @Test
    public void measureRunnableShouldSendInvocationTimeInSeconds() {
        withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measure(this::sleep20ms);

        assertClosureInvocationWrapped();
    }

    @Test
    public void measureRunnableShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        try {
            withPrefixAndTags.timer("time")
                    .tag("method", "elapsed")
                    .measure(this::sleep20msAndThrowRuntimeException);

        } catch (Exception ignore) {
        }

        assertClosureInvocationWrapped();
    }
    
    
    @Test
    public void wrappedRunnableShouldSendTimeInSecondsEveryInvocation() {
        Runnable runnable = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::sleep20ms);

        runnable.run();
        runnable.run();

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }


    /**
     * @see Timer#measureChecked(ashes.of.datadog.client.function.CheckedRunnable)
     * @see Timer#measureChecked(ashes.of.datadog.client.function.CheckedRunnable)
     */
    @Test
    public void measureCheckedRunnableShouldSendInvocationTimeInSeconds() throws Exception {
        withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measureChecked(this::checkedSleep20ms);

        assertClosureInvocationWrapped();
    }

    @Test
    public void measureCheckedRunnableShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        try {
            withPrefixAndTags.timer("time")
                    .tag("method", "elapsed")
                    .measureChecked(this::checkedSleep20msAndThrowException);

        } catch (Exception ignore) {
        }

        assertThat(server.poll(), matchMeasuredEvent());
    }

    /**
     * @see Timer#measure(Consumer, Object)
     * @see Timer#wrap(Consumer)
     */
    @Test
    public void wrappedConsumerShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        Consumer<String> c = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::consumerThatSleeps20ms);

        c.accept("Got it!");
        c.accept("Got it!");

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }

    /**
     * @see Timer#measure(BiConsumer, Object, Object)
     * @see Timer#wrap(BiConsumer)
     */
    @Test
    public void wrappedBiConsumerShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        BiConsumer<String, String> c = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::biConsumerThatSleeps20ms);

        c.accept("Got it!", "First");
        c.accept("Got it!", "Second");

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }

    /**
     * @see Timer#measure(Function, Object)
     * @see Timer#wrap(Function)
     */
    @Test
    public void wrappedFunctionShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        Function<String, String> f = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::functionThatSleeps20ms);

        assertEquals("Got it!", f.apply("Got it!"));
        assertEquals("Second chance", f.apply("Second chance"));

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }


    /**
     * @see Timer#measure(BiFunction, Object, Object)
     * @see Timer#wrap(BiFunction)
     */
    @Test
    public void wrappedBiFunctionShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        BiFunction<String, String, String> f = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::biFunctionThatSleeps20ms);

        assertEquals("Got it! Nothing", f.apply("Got it!", "Nothing"));
        assertEquals("One more time", f.apply("One more", "time"));

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }


    /**
     * @see Timer#measure(Supplier)
     * @see Timer#wrap(Supplier)
     */
    @Test
    public void measureSupplierShouldSendInvocationTimeInSeconds() {
        withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measure(this::stringSupplierThatSleeps20ms);

        assertClosureInvocationWrapped();
    }

    @Test
    public void measureSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        try {
            withPrefixAndTags.timer("time")
                    .tag("method", "elapsed")
                    .measure(this::stringSupplierThatSleeps20msAndThrowsRuntimeException);

        } catch (Exception ignore) {
        }

        assertClosureInvocationWrapped();
    }

    @Test
    public void wrappedSupplierShouldSendInvocationTimeInSeconds() {
        Supplier<String> supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::stringSupplierThatSleeps20ms);

        assertEquals("Got it!", supplier.get());
        assertEquals("Got it!", supplier.get());

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }

    @Test
    public void wrappedSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        Supplier<String> supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::stringSupplierThatSleeps20msAndThrowsRuntimeException);

        try {
            supplier.get();
            fail("No exception on supplier.get");
        } catch (Exception ignore) {
        }

        try {
            supplier.get();
            fail("No exception on supplier.get");
        } catch (Exception ignore) {
        }

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }

    /**
     * @see Timer#measure(IntSupplier)
     * @see Timer#wrap(IntSupplier)
     */
    @Test
    public void wrappedIntSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        IntSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::intSupplierThatSleeps20ms);

        assertEquals(1337, supplier.getAsInt());
        assertEquals(1337, supplier.getAsInt());
        
        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }


    /**
     * @see Timer#measure(LongSupplier)
     * @see Timer#wrap(LongSupplier)
     */
    @Test
    public void wrappedLongSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        LongSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::longSupplierThatSleeps20ms);

        assertEquals(31337, supplier.getAsLong());
        assertEquals(31337, supplier.getAsLong());

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }


    /**
     * @see Timer#measure(DoubleSupplier)
     * @see Timer#wrap(DoubleSupplier)
     */
    @Test
    public void wrappedDoubleSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        DoubleSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::doubleSupplierThatSleeps20ms);


        assertEquals(3.1337, supplier.getAsDouble(), 0.0001);
        assertEquals(3.1337, supplier.getAsDouble(), 0.0001);

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }


    /**
     * @see Timer#measure(BooleanSupplier)
     * @see Timer#wrap(BooleanSupplier)
     */
    @Test
    public void wrappedBooleanSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        BooleanSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .wrap(this::booleanSupplierThatSleeps20ms);

        assertEquals(true, supplier.getAsBoolean());
        assertEquals(true, supplier.getAsBoolean());

        assertClosureInvocationWrapped();
        assertClosureInvocationWrapped();
    }

    private void sleep20ms() {
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sleep20msAndThrowRuntimeException() {
        sleep20ms();
        throw new RuntimeException("Oups");
    }

    private void consumerThatSleeps20ms(String a) {
        sleep20ms();
    }

    private void biConsumerThatSleeps20ms(String a, String b) {
        sleep20ms();
    }


    private String functionThatSleeps20ms(String a) {
        sleep20ms();
        return a;
    }

    private String biFunctionThatSleeps20ms(String a, String b) {
        sleep20ms();
        return a + " " +  b;
    }


    private String stringSupplierThatSleeps20ms() {
        sleep20ms();
        return "Got it!";
    }

    private String stringSupplierThatSleeps20msAndThrowsRuntimeException() {
        sleep20ms();
        throw new RuntimeException("Oups");
    }

    private int intSupplierThatSleeps20ms() {
        sleep20ms();
        return 1337;
    }

    private long longSupplierThatSleeps20ms() {
        sleep20ms();
        return 31337;
    }
    
    private double doubleSupplierThatSleeps20ms() {
        sleep20ms();
        return 3.1337;
    }

    private boolean booleanSupplierThatSleeps20ms() {
        sleep20ms();
        return true;
    }
    



    private void checkedSleep20ms() throws Exception {
        sleep20ms();
    }

    private void checkedSleep20msAndThrowException() throws Exception {
        checkedSleep20ms();
        throw new Exception("Oups");
    }



    private Matcher<String> matchMeasuredEvent() {
        return Matchers.matchesPattern("test\\.time:\\d+\\.\\d+\\|h\\|#env:junit,method:elapsed");
    }

    private void assertClosureInvocationWrapped() {
        String event = server.poll();
        System.out.println(event);
        assertThat(event, Matchers.matchesPattern("test\\.time:\\d+\\.\\d+\\|h\\|#env:junit,method:elapsed"));
    }
}
