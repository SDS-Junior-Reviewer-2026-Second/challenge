import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Shared stdout-capturing helper for tests that assert on console output. */
final class ConsoleCapture {
    private ConsoleCapture() {}

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    static String captureStdOut(ThrowingRunnable action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
