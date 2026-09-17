package assemble.io;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** 미리 정해진 입력을 돌려주고 출력을 문자열로 모으는 테스트용 Console. delay 는 즉시 반환한다. */
public class FakeConsole implements Console {

    private final Deque<String> inputs;
    private final StringBuilder output = new StringBuilder();

    public FakeConsole(String... inputs) {
        this(List.of(inputs));
    }

    public FakeConsole(List<String> inputs) {
        this.inputs = new ArrayDeque<>(inputs);
    }

    /** Scanner 처럼 입력이 바닥나면 NoSuchElementException 을 던진다. */
    @Override
    public String readLine() {
        return inputs.removeFirst();
    }

    @Override
    public void print(String text) {
        output.append(text);
    }

    @Override
    public void println(String text) {
        output.append(text).append(System.lineSeparator());
    }

    @Override
    public void clear() {
        output.append(SystemConsole.CLEAR_SCREEN);
    }

    @Override
    public void delay(int millis) {
        // 테스트에서는 기다리지 않는다.
    }

    public String output() {
        return output.toString();
    }
}
