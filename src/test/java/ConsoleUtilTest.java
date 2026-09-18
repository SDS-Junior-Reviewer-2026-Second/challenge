import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class ConsoleUtilTest {

    @Test
    void delay_normalCall_doesNotThrow() {
        assertThatCode(() -> ConsoleUtil.delay(1)).doesNotThrowAnyException();
    }

    @Test
    void delay_interruptedThread_swallowsInterruptedException() {
        Thread.currentThread().interrupt();
        try {
            assertThatCode(() -> ConsoleUtil.delay(50)).doesNotThrowAnyException();
        } finally {
            Thread.interrupted(); // clear any leftover interrupt flag so it doesn't leak into other tests
        }
    }

    @Test
    void clearScreen_doesNotThrow() throws Exception {
        assertThatCode(() -> ConsoleCapture.captureStdOut(ConsoleUtil::clearScreen)).doesNotThrowAnyException();
    }
}
