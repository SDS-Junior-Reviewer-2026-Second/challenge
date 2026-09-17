import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntConsumer;

public class Assemble {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final int SELECTION_DELAY_MS = 800;
    private static final int TEST_START_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    private final Scanner scanner;
    private final PrintStream out;
    private final IntConsumer delay;
    private final CarConfiguration car = new CarConfiguration();
    private final CarService carService = new CarService();

    private Step step = Step.CAR_TYPE;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new Assemble(scanner, System.out, Assemble::sleep).run();
        scanner.close();
    }

    Assemble(Scanner scanner, PrintStream out, IntConsumer delay) {
        this.scanner = scanner;
        this.out = out;
        this.delay = delay;
    }

    void run() {
        while (true) {
            showCurrentMenu();

            String input = scanner.nextLine().trim();
            if ("exit".equalsIgnoreCase(input)) {
                out.println("바이바이");
                return;
            }

            Integer answer = parseInteger(input);
            if (answer == null) {
                showInputError("ERROR :: 숫자만 입력 가능");
                continue;
            }

            if (!step.accepts(answer)) {
                showInputError(step.errorMessage);
                continue;
            }

            if (answer == 0) {
                step = step.backStep();
                continue;
            }

            process(answer);
        }
    }

    private void showCurrentMenu() {
        out.print(CLEAR_SCREEN);
        out.flush();
        step.printMenu(out);
        out.print("INPUT > ");
    }

    private void showInputError(String message) {
        out.println(message);
        delay.accept(SELECTION_DELAY_MS);
    }

    private void process(int answer) {
        switch (step) {
            case CAR_TYPE:
                car.selectCarType(CarType.fromCode(answer));
                out.printf("차량 타입으로 %s을 선택하셨습니다.\n", car.carType().displayName());
                advanceTo(Step.ENGINE);
                break;
            case ENGINE:
                car.selectEngine(Engine.fromCode(answer));
                out.printf("%s 엔진을 선택하셨습니다.\n", car.engine().displayName());
                advanceTo(Step.BRAKE_SYSTEM);
                break;
            case BRAKE_SYSTEM:
                car.selectBrakeSystem(BrakeSystem.fromCode(answer));
                out.printf("%s 제동장치를 선택하셨습니다.\n", car.brakeSystem().selectionName());
                advanceTo(Step.STEERING_SYSTEM);
                break;
            case STEERING_SYSTEM:
                car.selectSteeringSystem(SteeringSystem.fromCode(answer));
                out.printf("%s 조향장치를 선택하셨습니다.\n", car.steeringSystem().selectionName());
                advanceTo(Step.RUN_TEST);
                break;
            case RUN_TEST:
                processCompletedCar(answer);
                break;
        }
    }

    private void advanceTo(Step nextStep) {
        delay.accept(SELECTION_DELAY_MS);
        step = nextStep;
    }

    private void processCompletedCar(int answer) {
        if (answer == 1) {
            printLines(carService.run(car));
            delay.accept(RESULT_DELAY_MS);
            return;
        }

        out.println("Test...");
        delay.accept(TEST_START_DELAY_MS);
        printLines(carService.test(car));
        delay.accept(RESULT_DELAY_MS);
    }

    private void printLines(List<String> lines) {
        for (String line : lines) {
            out.println(line);
        }
    }

    private Integer parseInteger(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
            // Preserve the original program's interruption behavior.
        }
    }

    private enum Step {
        CAR_TYPE(
                null,
                1, 3,
                "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능",
                "        ______________",
                "       /|            |",
                "  ____/_|_____________|____",
                " |                      O  |",
                " '-(@)----------------(@)--'",
                "===============================",
                "어떤 차량 타입을 선택할까요?",
                "1. Sedan",
                "2. SUV",
                "3. Truck",
                "==============================="),
        ENGINE(
                CAR_TYPE,
                0, 4,
                "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능",
                "어떤 엔진을 탑재할까요?",
                "0. 뒤로가기",
                "1. GM",
                "2. TOYOTA",
                "3. WIA",
                "4. 고장난 엔진",
                "==============================="),
        BRAKE_SYSTEM(
                ENGINE,
                0, 3,
                "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능",
                "어떤 제동장치를 선택할까요?",
                "0. 뒤로가기",
                "1. MANDO",
                "2. CONTINENTAL",
                "3. BOSCH",
                "==============================="),
        STEERING_SYSTEM(
                BRAKE_SYSTEM,
                0, 2,
                "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능",
                "어떤 조향장치를 선택할까요?",
                "0. 뒤로가기",
                "1. BOSCH",
                "2. MOBIS",
                "==============================="),
        RUN_TEST(
                CAR_TYPE,
                0, 2,
                "ERROR :: Run 또는 Test 중 하나를 선택 필요",
                "멋진 차량이 완성되었습니다.",
                "어떤 동작을 할까요?",
                "0. 처음 화면으로 돌아가기",
                "1. RUN",
                "2. Test",
                "===============================");

        private final Step previousStep;
        private final int minAnswer;
        private final int maxAnswer;
        private final String errorMessage;
        private final String[] menuLines;

        Step(Step previousStep, int minAnswer, int maxAnswer, String errorMessage, String... menuLines) {
            this.previousStep = previousStep;
            this.minAnswer = minAnswer;
            this.maxAnswer = maxAnswer;
            this.errorMessage = errorMessage;
            this.menuLines = menuLines;
        }

        private boolean accepts(int answer) {
            return minAnswer <= answer && answer <= maxAnswer;
        }

        private void printMenu(PrintStream out) {
            for (String line : menuLines) {
                out.println(line);
            }
        }

        private Step backStep() {
            return previousStep;
        }
    }
}
