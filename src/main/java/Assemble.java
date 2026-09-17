import domain.model.BrakeSystem;
import domain.model.CarConfig;
import domain.model.CarType;
import domain.model.Engine;
import domain.model.SteeringSystem;
import domain.rule.CompatibilityChecker;

import java.util.List;
import java.util.Scanner;

public class Assemble {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private enum Step {
        CAR_TYPE, ENGINE, BRAKE_SYSTEM, STEERING_SYSTEM, RUN_TEST
    }

    private static final CarConfig config = new CarConfig();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Step step = Step.CAR_TYPE;

        while (step != null) {
            step = runStep(sc, step);
        }

        sc.close();
    }

    // ---- Step-loop orchestration ----

    private static Step runStep(Scanner sc, Step step) {
        clearScreen();
        showMenu(step);

        String input = readUserInput(sc);
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("바이바이");
            return null;
        }

        Integer answer = parseAnswer(input);
        if (answer == null || !isValidRange(step, answer)) {
            delay(800);
            return step;
        }

        if (answer == 0) {
            return previousStep(step);
        }

        return advance(step, answer);
    }

    private static String readUserInput(Scanner sc) {
        System.out.print("INPUT > ");
        return sc.nextLine().trim();
    }

    private static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    private static Step previousStep(Step step) {
        switch (step) {
            case ENGINE: return Step.CAR_TYPE;
            case BRAKE_SYSTEM: return Step.ENGINE;
            case STEERING_SYSTEM: return Step.BRAKE_SYSTEM;
            case RUN_TEST: return Step.CAR_TYPE;
            default: return step;
        }
    }

    private static Step advance(Step step, int answer) {
        switch (step) {
            case CAR_TYPE:
                selectCarType(answer);
                delay(800);
                return Step.ENGINE;
            case ENGINE:
                selectEngine(answer);
                delay(800);
                return Step.BRAKE_SYSTEM;
            case BRAKE_SYSTEM:
                selectBrakeSystem(answer);
                delay(800);
                return Step.STEERING_SYSTEM;
            case STEERING_SYSTEM:
                selectSteeringSystem(answer);
                delay(800);
                return Step.RUN_TEST;
            case RUN_TEST:
                handleRunTestAnswer(answer);
                return Step.RUN_TEST;
            default:
                return step;
        }
    }

    private static void handleRunTestAnswer(int answer) {
        if (answer == 1) {
            runProducedCar();
            delay(2000);
        } else if (answer == 2) {
            System.out.println("Test...");
            delay(1500);
            testProducedCar();
            delay(2000);
        }
    }

    // ---- Menu display ----

    private static void showMenu(Step step) {
        switch (step) {
            case CAR_TYPE:
                showCarTypeMenu(); break;
            case ENGINE:
                showEngineMenu(); break;
            case BRAKE_SYSTEM:
                showBrakeMenu(); break;
            case STEERING_SYSTEM:
                showSteeringMenu(); break;
            case RUN_TEST:
                showRunTestMenu(); break;
        }
    }

    private static void showCarTypeMenu() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        System.out.println("1. Sedan");
        System.out.println("2. SUV");
        System.out.println("3. Truck");
        System.out.println("===============================");
    }
    private static void showEngineMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. GM");
        System.out.println("2. TOYOTA");
        System.out.println("3. WIA");
        System.out.println("4. 고장난 엔진");
        System.out.println("===============================");
    }
    private static void showBrakeMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. MANDO");
        System.out.println("2. CONTINENTAL");
        System.out.println("3. BOSCH");
        System.out.println("===============================");
    }
    private static void showSteeringMenu() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. BOSCH");
        System.out.println("2. MOBIS");
        System.out.println("===============================");
    }
    private static void showRunTestMenu() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
        System.out.println("===============================");
    }

    // ---- Input validation ----

    private static Integer parseAnswer(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("ERROR :: 숫자만 입력 가능");
            return null;
        }
    }

    private static boolean isValidRange(Step step, int answer) {
        switch (step) {
            case CAR_TYPE:
                return checkRange(answer, 1, 3, "차량 타입은 1 ~ 3 범위만 선택 가능");
            case ENGINE:
                return checkRange(answer, 0, 4, "엔진은 1 ~ 4 범위만 선택 가능");
            case BRAKE_SYSTEM:
                return checkRange(answer, 0, 3, "제동장치는 1 ~ 3 범위만 선택 가능");
            case STEERING_SYSTEM:
                return checkRange(answer, 0, 2, "조향장치는 1 ~ 2 범위만 선택 가능");
            case RUN_TEST:
                return checkRange(answer, 0, 2, "Run 또는 Test 중 하나를 선택 필요");
            default:
                return true;
        }
    }

    private static boolean checkRange(int answer, int min, int max, String errorMessage) {
        if (answer < min || answer > max) {
            System.out.println("ERROR :: " + errorMessage);
            return false;
        }
        return true;
    }

    // ---- Selection handlers ----

    private static void selectCarType(int code) {
        config.setCarType(CarType.fromCode(code));
        System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n", config.getCarType().getLabel());
    }
    private static void selectEngine(int code) {
        config.setEngine(Engine.fromCode(code));
        System.out.printf("%s 엔진을 선택하셨습니다.\n", config.getEngine().getLabel());
    }
    private static void selectBrakeSystem(int code) {
        config.setBrake(BrakeSystem.fromCode(code));
        System.out.printf("%s 제동장치를 선택하셨습니다.\n", config.getBrake().getLabel());
    }
    private static void selectSteeringSystem(int code) {
        config.setSteering(SteeringSystem.fromCode(code));
        System.out.printf("%s 조향장치를 선택하셨습니다.\n", config.getSteering().getLabel());
    }

    // ---- Run / Test ----

    private static void runProducedCar() {
        if (!CompatibilityChecker.isValid(config)) {
            printIncompatibleMessage();
            return;
        }
        if (config.getEngine() == Engine.BROKEN) {
            printBrokenEngineMessage();
            return;
        }
        printCarSummary();
    }

    private static void printIncompatibleMessage() {
        System.out.println("자동차가 동작되지 않습니다");
    }

    private static void printBrokenEngineMessage() {
        System.out.println("엔진이 고장나있습니다.");
        System.out.println("자동차가 움직이지 않습니다.");
    }

    private static void printCarSummary() {
        System.out.printf("Car Type : %s\n", config.getCarType().getLabel());
        System.out.printf("Engine   : %s\n", config.getEngine().getLabel());
        System.out.printf("Brake    : %s\n", config.getBrake().getLabel());
        System.out.printf("Steering : %s\n", config.getSteering().getLabel());
        System.out.println("자동차가 동작됩니다.");
    }

    private static void testProducedCar() {
        List<String> violations = CompatibilityChecker.findViolations(config);
        if (violations.isEmpty()) {
            System.out.println("자동차 부품 조합 테스트 결과 : PASS");
        } else {
            fail(violations.get(0));
        }
    }

    private static void fail(String message) {
        System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
        System.out.println(message);
    }

    // ---- Utility ----

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
