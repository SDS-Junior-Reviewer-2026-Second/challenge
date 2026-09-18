package mission2;

import mission2.car.Car;
import mission2.car.CarAction;
import mission2.car.part.Part;
import mission2.display.CarView;
import mission2.display.ConsoleDisplay;
import mission2.display.Display;
import mission2.display.step.Step;
import mission2.display.step.StepFactory;
import mission2.display.step.StepListener;

import java.util.List;
import java.util.OptionalInt;

public class Assemble implements StepListener {

    private static final String EXIT = "exit";
    private static final long READ_MESSAGE = 800;
    private static final long TESTING = 1500;
    private static final long READ_RESULT = 2000;

    private final Display display;
    private final List<Step> steps;

    private Car car = new Car();
    private int current = 0;
    private boolean assembling = true;

    public Assemble(Display display, List<Step> steps) {
        this.display = display;
        this.steps = steps;
    }

    public static void main(String[] args) {
        new Assemble(new ConsoleDisplay(System.in, System.out), StepFactory.assemblyLine()).run();
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

    @Override
    public void partSelected(Part part) {
        car.install(part);
        announce(part.selectionMessage(), READ_MESSAGE);
        current++;
    }

    @Override
    public void actionSelected(CarAction action) {
        switch (action) {
            case RUN -> display.show(CarView.runLines(car));
            case TEST -> {
                announce("Test...", TESTING);
                display.show(CarView.testLines(car));
            }
        }
        display.pause(READ_RESULT);
    }

    private void respondTo(String input) {
        if (EXIT.equalsIgnoreCase(input)) {
            assembling = false;
            return;
        }
        OptionalInt code = codeOf(input);
        if (code.isEmpty()) {
            warn("숫자만 입력 가능");
            return;
        }
        choose(code.getAsInt());
    }

    private void choose(int code) {
        if (!step().accepts(code)) warn(step().rangeError());
        else if (code == Step.BACK) goBack();
        else step().select(code, this);
    }

    private void goBack() {
        boolean fromLastStep = current == steps.size() - 1;
        if (fromLastStep) car = new Car();
        current = fromLastStep ? 0 : current - 1;
    }

    private Step step() {
        return steps.get(current);
    }

    private void warn(String message) {
        announce("ERROR :: " + message, READ_MESSAGE);
    }

    private void announce(String message, long readingTime) {
        display.show(message);
        display.pause(readingTime);
    }

    private static OptionalInt codeOf(String input) {
        try {
            return OptionalInt.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
