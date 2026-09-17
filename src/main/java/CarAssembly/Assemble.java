package CarAssembly;

import CarEnums.AssembleStep;

import java.util.Optional;
import java.util.Scanner;

public class Assemble {

    private static final int BACK_TO_START = 0;
    private static final int RUN = 1;
    private static final int TEST = 2;

    private static final String[] RUN_TEST_MENU = {
            "멋진 차량이 완성되었습니다.",
            "어떤 동작을 할까요?",
            "0. 처음 화면으로 돌아가기",
            "1. RUN",
            "2. Test",
            "==============================="
    };

    private final ConsoleView view = new ConsoleView();
    private final CarValidator validator = new CarValidator();
    private final Scanner scanner = new Scanner(System.in);

    private CarBuilder carBuilder = new CarBuilder();
    private AssembleStep currentStep = AssembleStep.CAR_TYPE;
    private boolean assemblyCompleted = false;

    public static void main(String[] args) {
        new Assemble().run();
    }

    public void run() {
        while (true) {
            view.clearScreen();
            showCurrentMenu();

            System.out.print("INPUT > ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                view.printLine("바이바이");
                break;
            }

            Integer answer = parseAnswer(input);
            if (answer == null) {
                pause();
                continue;
            }

            if (assemblyCompleted) {
                handleRunTestInput(answer);
            } else {
                handleAssembleInput(answer);
            }
        }
        scanner.close();
    }

    private void showCurrentMenu() {
        if (assemblyCompleted) {
            view.printMenu(RUN_TEST_MENU);
        } else {
            view.printMenu(currentStep.menuLines());
        }
    }

    private Integer parseAnswer(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            view.printError("숫자만 입력 가능");
            return null;
        }
    }

    private void handleAssembleInput(int answer) {
        if (currentStep.isBackAnswer(answer) && currentStep.canGoBack()) {
            currentStep = currentStep.previous();
            return;
        }

        if (!currentStep.isInSelectableRange(answer)) {
            view.printError(currentStep.rangeErrorMessage());
            pause();
            return;
        }

        String message = currentStep.select(answer, carBuilder);
        view.printLine(message);
        pause();

        AssembleStep next = currentStep.next();
        if (next == null) {
            assemblyCompleted = true;
        } else {
            currentStep = next;
        }
    }

    private void handleRunTestInput(int answer) {
        if (answer == BACK_TO_START) {
            resetAssembly();
        } else if (answer == RUN) {
            runProducedCar();
            pause(2000);
        } else if (answer == TEST) {
            System.out.println("Test...");
            pause(1500);
            testProducedCar();
            pause(2000);
        } else {
            view.printError("Run 또는 Test 중 하나를 선택 필요");
            pause();
        }
    }

    private void resetAssembly() {
        carBuilder = new CarBuilder();
        currentStep = AssembleStep.CAR_TYPE;
        assemblyCompleted = false;
    }

    private void runProducedCar() {
        Car car = carBuilder.build();

        Optional<String> violation = validator.findFirstViolation(car);
        if (violation.isPresent()) {
            view.printLine("자동차가 동작되지 않습니다");
            return;
        }
        if (car.hasBrokenEngine()) {
            view.printLine("엔진이 고장나있습니다.");
            view.printLine("자동차가 움직이지 않습니다.");
            return;
        }
        view.printLine(car.describe());
        view.printLine("자동차가 동작됩니다.");
    }

    private void testProducedCar() {
        Car car = carBuilder.build();
        Optional<String> violation = validator.findFirstViolation(car);

        if (violation.isPresent()) {
            view.printLine("자동차 부품 조합 테스트 결과 : FAIL");
            view.printLine(violation.get());
        } else {
            view.printLine("자동차 부품 조합 테스트 결과 : PASS");
        }
    }

    private void pause() {
        pause(800);
    }

    private void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}