import java.util.List;

public interface Display {

    void redraw(List<String> screen);

    void show(List<String> lines);

    default void show(String line) {
        show(List.of(line));
    }

    String ask();

    void pause(long millis);
}
