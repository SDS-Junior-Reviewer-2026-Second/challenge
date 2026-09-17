import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class ConsoleDisplay implements Display {

    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final String INPUT_PROMPT = "INPUT > ";

    private final Scanner in;
    private final PrintStream out;
    private final boolean animated;

    public ConsoleDisplay(InputStream in, PrintStream out, boolean animated) {
        this.in = new Scanner(in);
        this.out = out;
        this.animated = animated;
    }

    @Override
    public void redraw(List<String> screen) {
        if (animated) {
            out.print(CLEAR_SCREEN);
        }
        show(screen);
    }

    @Override
    public void show(List<String> lines) {
        lines.forEach(out::println);
    }

    @Override
    public String ask() {
        out.print(INPUT_PROMPT);
        out.flush();
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    @Override
    public void pause(long millis) {
        try {
            Thread.sleep(animated ? millis : 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
