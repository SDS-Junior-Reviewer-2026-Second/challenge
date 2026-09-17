import java.util.Scanner;

public class Assemble {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private static Car car = new Car();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AssemblyFlow flow = new AssemblyFlow();

        while (true) {
            AssemblyStep step = flow.currentStep();
            System.out.print(CLEAR_SCREEN);
            System.out.flush();

            step.showMenu();

            System.out.print("INPUT > ");
            String buf = sc.nextLine().trim();

            if (buf.equalsIgnoreCase("exit")) {
                System.out.println("바이바이");
                break;
            }

            int answer;
            try {
                answer = Integer.parseInt(buf);
            } catch (NumberFormatException e) {
                System.out.println("ERROR :: 숫자만 입력 가능");
                delay(800);
                continue;
            }

            if (!step.isValidInput(answer)) {
                System.out.println(step.validationError());
                delay(800);
                continue;
            }

            if (answer == AssemblyStep.BACK) {
                flow.moveBack();
                continue;
            }

            if (step.isRunTestStep()) {
                handleRunTest(answer);
                continue;
            }

            step.select(car, answer);
            delay(800);
            flow.moveNext();
        }

        sc.close();
    }

    private static void handleRunTest(int answer) {
        if (answer == AssemblyStep.RUN) {
            car.runProducedCar();
            delay(2000);
        } else if (answer == AssemblyStep.TEST) {
            System.out.println("Test...");
            delay(1500);
            car.testProducedCar();
            delay(2000);
        }
    }

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
