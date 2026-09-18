package assemble;

import assemble.ui.StepAction;
import car.Car;

import java.util.Scanner;

public class Assemble {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private final Car car = new Car();
    private final AssemblyFlow flow = new AssemblyFlow();
    private final Scanner scanner;

    public Assemble(Scanner scanner) {
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new Assemble(scanner).run();
        }
    }

    public void run() {
        while (processCurrentStep()) {
            // 다음 입력을 기다린다.
        }
    }

    private boolean processCurrentStep() {
        AssemblyStep step = flow.currentStep();
        showStep(step);

        String input = readInput();
        if (isExit(input)) {
            System.out.println("바이바이");
            return false;
        }

        Integer answer = parseAnswer(input);
        if (answer == null) {
            return true;
        }

        processAnswer(step, answer);
        return true;
    }

    private void showStep(AssemblyStep step) {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
        step.showMenu();
    }

    private String readInput() {
        System.out.print("INPUT > ");
        return scanner.nextLine().trim();
    }

    private boolean isExit(String input) {
        return input.equalsIgnoreCase("exit");
    }

    private Integer parseAnswer(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("ERROR :: 숫자만 입력 가능");
            delay(800);
            return null;
        }
    }

    private void processAnswer(AssemblyStep step, int answer) {
        if (!step.isValidInput(answer)) {
            System.out.println(step.validationError());
            delay(800);
            return;
        }

        if (answer == AssemblyStep.BACK) {
            flow.moveBack();
            return;
        }

        StepAction action = step.execute(car, answer);
        if (action == StepAction.NEXT) {
            delay(800);
            flow.moveNext();
        }
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
