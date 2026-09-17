public class Assemble {

    private static final String EXIT = "exit";
    private static final int NOT_A_NUMBER = -1;
    private static final long READ_MESSAGE = 800;
    private static final long READ_RESULT = 2000;

    private final Display display;
    private final Step[] steps;

    private Car car = new Car();
    private int current = 0;
    private boolean assembling = true;

    public Assemble(Display display, Step[] steps) {
        this.display = display;
        this.steps = steps;
    }

    public static void main(String[] args) {
        AssemblyFactory.createConsoleApp(System.in, System.out, true).run();
    }

    public void run() {
        while (assembling) {
            display.redraw(step().lines());
            String input = display.ask();
            if (input == null) break;
            respondTo(input);
        }
        display.show("바이바이");
    }

    private void respondTo(String input) {
        int code = codeOf(input);

        if (EXIT.equalsIgnoreCase(input)) assembling = false;
        else if (code == NOT_A_NUMBER) warn("숫자만 입력 가능");
        else if (!step().accepts(code)) warn(step().rangeError());
        else if (code == Step.BACK) goBack();
        else choose(step().option(code));
    }

    private void choose(String chosen) {
        if (step().isAction()) {
            perform(chosen);
            return;
        }
        car.install(step().partSlot(), chosen);
        announce("[%s] %s 선택 완료".formatted(step().title(), chosen), READ_MESSAGE);
        current++;
    }

    private void perform(String action) {
        if (Car.TEST.equals(action)) announce("Test...", READ_RESULT);
        display.show(car.perform(action));
        display.pause(READ_RESULT);
    }

    private void goBack() {
        boolean fromLastScreen = step().isAction();
        if (fromLastScreen) car = new Car();
        current = fromLastScreen ? 0 : current - 1;
    }

    private Step step() {
        return steps[current];
    }

    private void warn(String message) {
        announce("ERROR :: " + message, READ_MESSAGE);
    }

    private void announce(String message, long readingTime) {
        display.show(message);
        display.pause(readingTime);
    }

    private static int codeOf(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return NOT_A_NUMBER;
        }
    }
}
