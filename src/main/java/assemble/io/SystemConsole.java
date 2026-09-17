package assemble.io;

import java.util.Optional;
import java.util.Scanner;

/** System.in / System.out 기반 Console 구현. */
public class SystemConsole implements Console {
    public static final String CLEAR_SCREEN = "\033[H\033[2J";

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public Optional<String> readLine() {
        return scanner.hasNextLine() ? Optional.of(scanner.nextLine()) : Optional.empty();
    }

    @Override
    public void print(String text) {
        System.out.print(text);
        System.out.flush();
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }

    @Override
    public void clear() {
        print(CLEAR_SCREEN);
    }

    @Override
    public void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    public void close() {
        scanner.close();
    }
}
