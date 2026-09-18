import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

final class ConsoleView {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final String SEPARATOR = "===============================";

    private final Scanner scanner;
    private final PrintStream out;

    ConsoleView(Scanner scanner, PrintStream out) {
        this.scanner = Objects.requireNonNull(scanner);
        this.out = Objects.requireNonNull(out);
    }

    void showMenu(AssemblyStep step) {
        out.print(CLEAR_SCREEN);
        out.flush();

        switch (step) {
            case CAR_TYPE -> showCarTypeMenu();
            case ENGINE -> showEngineMenu();
            case BRAKE_SYSTEM -> showBrakeMenu();
            case STEERING_SYSTEM -> showSteeringMenu();
            case RUN_TEST -> showRunTestMenu();
        }
    }

    String readInput() {
        out.print("INPUT > ");
        return scanner.nextLine().trim();
    }

    void showInputError(String message) {
        out.println(message);
    }

    void showGoodbye() {
        out.println("바이바이");
    }

    void showCarTypeSelection(CarType carType) {
        out.printf("차량 타입으로 %s을 선택하셨습니다.\n", carType.displayName());
    }

    void showEngineSelection(Engine engine) {
        out.printf("%s 엔진을 선택하셨습니다.\n", engine.displayName());
    }

    void showBrakeSelection(BrakeSystem brakeSystem) {
        out.printf("%s 제동장치를 선택하셨습니다.\n", brakeSystem.selectionName());
    }

    void showSteeringSelection(SteeringSystem steeringSystem) {
        out.printf("%s 조향장치를 선택하셨습니다.\n", steeringSystem.selectionName());
    }

    void showRunResult(CarConfiguration car, CarService.RunStatus result) {
        switch (result) {
            case INCOMPATIBLE -> out.println("자동차가 동작되지 않습니다");
            case BROKEN_ENGINE -> {
                out.println("엔진이 고장나있습니다.");
                out.println("자동차가 움직이지 않습니다.");
            }
            case RUNNING -> {
                // 원본은 아래 4개 줄에 printf("...\\n")를 사용한다.
                out.printf("Car Type : %s\n", car.carType().displayName());
                out.printf("Engine   : %s\n", car.engine().displayName());
                out.printf("Brake    : %s\n", car.brakeSystem().runName());
                out.printf("Steering : %s\n", car.steeringSystem().runName());
                out.println("자동차가 동작됩니다.");
            }
        }
    }

    void showTestStarted() {
        out.println("Test...");
    }

    void showTestResult(Optional<String> failure) {
        if (failure.isPresent()) {
            out.println("자동차 부품 조합 테스트 결과 : FAIL");
            out.println(failure.get());
            return;
        }
        out.println("자동차 부품 조합 테스트 결과 : PASS");
    }

    private void showCarTypeMenu() {
        out.println("        ______________");
        out.println("       /|            |");
        out.println("  ____/_|_____________|____");
        out.println(" |                      O  |");
        out.println(" '-(@)----------------(@)--'");
        out.println(SEPARATOR);
        out.println("어떤 차량 타입을 선택할까요?");
        for (CarType value : CarType.values()) {
            out.println(value.code() + ". " + value.displayName());
        }
        out.println(SEPARATOR);
    }

    private void showEngineMenu() {
        out.println("어떤 엔진을 탑재할까요?");
        out.println("0. 뒤로가기");
        for (Engine value : Engine.values()) {
            out.println(value.code() + ". " + value.displayName());
        }
        out.println(SEPARATOR);
    }

    private void showBrakeMenu() {
        out.println("어떤 제동장치를 선택할까요?");
        out.println("0. 뒤로가기");
        for (BrakeSystem value : BrakeSystem.values()) {
            out.println(value.code() + ". " + value.selectionName());
        }
        out.println(SEPARATOR);
    }

    private void showSteeringMenu() {
        out.println("어떤 조향장치를 선택할까요?");
        out.println("0. 뒤로가기");
        for (SteeringSystem value : SteeringSystem.values()) {
            out.println(value.code() + ". " + value.selectionName());
        }
        out.println(SEPARATOR);
    }

    private void showRunTestMenu() {
        out.println("멋진 차량이 완성되었습니다.");
        out.println("어떤 동작을 할까요?");
        out.println("0. 처음 화면으로 돌아가기");
        out.println("1. RUN");
        out.println("2. Test");
        out.println(SEPARATOR);
    }
}
