package org.example.assemble;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

public final class ConsoleUserInterface implements UserInterface, AutoCloseable {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private final Scanner scanner;
    private final PrintStream output;

    public ConsoleUserInterface(Scanner scanner, PrintStream output) {
        this.scanner = Objects.requireNonNull(scanner, "scanner");
        this.output = Objects.requireNonNull(output, "output");
    }

    @Override
    public void showMenu(AssemblyStep step) {
        output.print(CLEAR_SCREEN);
        output.flush();

        switch (step) {
            case CAR_TYPE -> showCarTypeMenu();
            case ENGINE -> showEngineMenu();
            case BRAKE_SYSTEM -> showBrakeMenu();
            case STEERING_SYSTEM -> showSteeringMenu();
            case RUN_TEST -> showRunTestMenu();
        }
    }

    @Override
    public String readInput() {
        output.print("INPUT > ");
        output.flush();
        return scanner.nextLine();
    }

    @Override
    public void showNumberRequired() {
        output.println("ERROR :: 숫자만 입력 가능");
    }

    @Override
    public void showRangeError(AssemblyStep step) {
        output.println(step.rangeErrorMessage());
    }

    @Override
    public void showSelection(CarType carType) {
        output.printf("차량 타입으로 %s을 선택하셨습니다.\n", carType.displayName());
    }

    @Override
    public void showSelection(Engine engine) {
        output.printf("%s 엔진을 선택하셨습니다.\n", engine.displayName());
    }

    @Override
    public void showSelection(BrakeSystem brakeSystem) {
        output.printf("%s 제동장치를 선택하셨습니다.\n", brakeSystem.selectionName());
    }

    @Override
    public void showSelection(SteeringSystem steeringSystem) {
        output.printf("%s 조향장치를 선택하셨습니다.\n", steeringSystem.selectionName());
    }

    @Override
    public void showInvalidCar() {
        output.println("자동차가 동작되지 않습니다");
    }

    @Override
    public void showBrokenEngine() {
        output.println("엔진이 고장나있습니다.");
        output.println("자동차가 움직이지 않습니다.");
    }

    @Override
    public void showRunningCar(VehicleConfiguration configuration) {
        output.printf("Car Type : %s\n", configuration.carType().displayName());
        output.printf("Engine   : %s\n", configuration.engine().displayName());
        output.printf("Brake    : %s\n", configuration.brakeSystem().runName());
        output.printf("Steering : %s\n", configuration.steeringSystem().runName());
        output.println("자동차가 동작됩니다.");
    }

    @Override
    public void showTestStarted() {
        output.println("Test...");
    }

    @Override
    public void showTestResult(CompatibilityResult result) {
        if (result.valid()) {
            output.println("자동차 부품 조합 테스트 결과 : PASS");
            return;
        }
        output.println("자동차 부품 조합 테스트 결과 : FAIL");
        output.println(result.failureReason());
    }

    @Override
    public void showGoodbye() {
        output.println("바이바이");
    }

    @Override
    public void close() {
        scanner.close();
    }

    private void showCarTypeMenu() {
        output.println("        ______________");
        output.println("       /|            |");
        output.println("  ____/_|_____________|____");
        output.println(" |                      O  |");
        output.println(" '-(@)----------------(@)--'");
        output.println("===============================");
        output.println("어떤 차량 타입을 선택할까요?");
        output.println("1. Sedan");
        output.println("2. SUV");
        output.println("3. Truck");
        output.println("===============================");
    }

    private void showEngineMenu() {
        output.println("어떤 엔진을 탑재할까요?");
        output.println("0. 뒤로가기");
        output.println("1. GM");
        output.println("2. TOYOTA");
        output.println("3. WIA");
        output.println("4. 고장난 엔진");
        output.println("===============================");
    }

    private void showBrakeMenu() {
        output.println("어떤 제동장치를 선택할까요?");
        output.println("0. 뒤로가기");
        output.println("1. MANDO");
        output.println("2. CONTINENTAL");
        output.println("3. BOSCH");
        output.println("===============================");
    }

    private void showSteeringMenu() {
        output.println("어떤 조향장치를 선택할까요?");
        output.println("0. 뒤로가기");
        output.println("1. BOSCH");
        output.println("2. MOBIS");
        output.println("===============================");
    }

    private void showRunTestMenu() {
        output.println("멋진 차량이 완성되었습니다.");
        output.println("어떤 동작을 할까요?");
        output.println("0. 처음 화면으로 돌아가기");
        output.println("1. RUN");
        output.println("2. Test");
        output.println("===============================");
    }
}
