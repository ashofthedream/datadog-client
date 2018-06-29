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
     * @see Timer#measured(Runnable)
     */
    @Test
    public void measureRunnableShouldSendInvocationTimeInSeconds() {
        withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measure(this::sleep20ms);

        assertClosureInvocationMeasured();
    }

    @Test
    public void measureRunnableShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        try {
            withPrefixAndTags.timer("time")
                    .tag("method", "elapsed")
                    .measure(this::sleep20msAndThrowRuntimeException);

        } catch (Exception ignore) {
        }

        assertClosureInvocationMeasured();
    }
    
    
    @Test
    public void measuredRunnableShouldSendTimeInSecondsEveryInvocation() {
        Runnable runnable = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::sleep20ms);

        runnable.run();
        runnable.run();

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
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

        assertClosureInvocationMeasured();
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
     * @see Timer#measured(Consumer)
     */
    @Test
    public void measuredConsumerShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        Consumer<String> c = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::consumerThatSleeps20ms);

        c.accept("Got it!");
        c.accept("Got it!");

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }

    /**
     * @see Timer#measure(BiConsumer, Object, Object)
     * @see Timer#measured(BiConsumer)
     */
    @Test
    public void measuredBiConsumerShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        BiConsumer<String, String> c = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::biConsumerThatSleeps20ms);

        c.accept("Got it!", "First");
        c.accept("Got it!", "Second");

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }

    /**
     * @see Timer#measure(Function, Object)
     * @see Timer#measured(Function)
     */
    @Test
    public void measuredFunctionShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        Function<String, String> f = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::functionThatSleeps20ms);

        assertEquals("Got it!", f.apply("Got it!"));
        assertEquals("Second chance", f.apply("Second chance"));

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }


    /**
     * @see Timer#measure(BiFunction, Object, Object)
     * @see Timer#measured(BiFunction)
     */
    @Test
    public void measuredBiFunctionShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        BiFunction<String, String, String> f = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::biFunctionThatSleeps20ms);

        assertEquals("Got it! Nothing", f.apply("Got it!", "Nothing"));
        assertEquals("One more time", f.apply("One more", "time"));

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }


    /**
     * @see Timer#measure(Supplier)
     * @see Timer#measured(Supplier)
     */
    @Test
    public void measureSupplierShouldSendInvocationTimeInSeconds() {
        withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measure(this::stringSupplierThatSleeps20ms);

        assertClosureInvocationMeasured();
    }

    @Test
    public void measureSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        try {
            withPrefixAndTags.timer("time")
                    .tag("method", "elapsed")
                    .measure(this::stringSupplierThatSleeps20msAndThrowsRuntimeException);

        } catch (Exception ignore) {
        }

        assertClosureInvocationMeasured();
    }

    @Test
    public void measuredSupplierShouldSendInvocationTimeInSeconds() {
        Supplier<String> supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::stringSupplierThatSleeps20ms);

        assertEquals("Got it!", supplier.get());
        assertEquals("Got it!", supplier.get());

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }

    @Test
    public void measuredSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        Supplier<String> supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::stringSupplierThatSleeps20msAndThrowsRuntimeException);

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

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }

    /**
     * @see Timer#measure(IntSupplier)
     * @see Timer#measured(IntSupplier)
     */
    @Test
    public void measuredIntSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        IntSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::intSupplierThatSleeps20ms);

        assertEquals(1337, supplier.getAsInt());
        assertEquals(1337, supplier.getAsInt());
        
        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }


    /**
     * @see Timer#measure(LongSupplier)
     * @see Timer#measured(LongSupplier)
     */
    @Test
    public void measuredLongSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        LongSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::longSupplierThatSleeps20ms);

        assertEquals(31337, supplier.getAsLong());
        assertEquals(31337, supplier.getAsLong());

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }


    /**
     * @see Timer#measure(DoubleSupplier)
     * @see Timer#measured(DoubleSupplier)
     */
    @Test
    public void measuredDoubleSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        DoubleSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::doubleSupplierThatSleeps20ms);


        assertEquals(3.1337, supplier.getAsDouble(), 0.0001);
        assertEquals(3.1337, supplier.getAsDouble(), 0.0001);

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
    }


    /**
     * @see Timer#measure(BooleanSupplier)
     * @see Timer#measured(BooleanSupplier)
     */
    @Test
    public void measuredBooleanSupplierShouldSendInvocationTimeInSecondsEvenIfMethodThrowsAnException() {
        BooleanSupplier supplier = withPrefixAndTags.timer("time")
                .tag("method", "elapsed")
                .measured(this::booleanSupplierThatSleeps20ms);

        assertEquals(true, supplier.getAsBoolean());
        assertEquals(true, supplier.getAsBoolean());

        assertClosureInvocationMeasured();
        assertClosureInvocationMeasured();
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

    private void assertClosureInvocationMeasured() {
        String event = server.poll();
        System.out.println(event);
        assertThat(event, Matchers.matchesPattern("test\\.time:\\d+\\.\\d+\\|h\\|#env:junit,method:elapsed"));
    }
}
