package carassembly.console;

import carassembly.application.AssemblyStep;
import carassembly.domain.Car;
import carassembly.domain.RunStatus;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/** 콘솔 입력과 화면 출력만 담당한다. 부품 호환성은 판단하지 않는다. */
public final class ConsoleView {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final String SEPARATOR = "===============================";

    private final Scanner input;
    private final PrintStream output;

    public ConsoleView(Scanner input, PrintStream output) {
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
    }

    public Optional<String> readCommand() {
        output.print("INPUT > ");
        output.flush();
        return input.hasNextLine() ? Optional.of(input.nextLine().trim()) : Optional.empty();
    }

    public void showMenu(AssemblyStep step) {
        output.print(CLEAR_SCREEN);
        output.flush();
        if (step == AssemblyStep.CAR_TYPE) {
            showCarPicture();
        } else if (step == AssemblyStep.RUN_TEST) {
            output.println("멋진 차량이 완성되었습니다.");
        }

        output.println(step.question());
        if (step != AssemblyStep.CAR_TYPE) {
            output.println(step == AssemblyStep.RUN_TEST
                    ? "0. 처음 화면으로 돌아가기" : "0. 뒤로가기");
        }
        for (int i = 0; i < step.options().size(); i++) {
            output.printf("%d. %s%n", i + 1, step.options().get(i));
        }
        output.println(SEPARATOR);
    }

    private void showCarPicture() {
        output.println("        ______________");
        output.println("       /|            |");
        output.println("  ____/_|_____________|____");
        output.println(" |                      O  |");
        output.println(" '-(@)----------------(@)--'");
        output.println(SEPARATOR);
    }

    public void showSelection(AssemblyStep step, Object component) {
        String format = switch (step) {
            case CAR_TYPE -> "차량 타입으로 %s을 선택하셨습니다.\n";
            case ENGINE -> "%s 엔진을 선택하셨습니다.\n";
            case BRAKE_SYSTEM -> "%s 제동장치를 선택하셨습니다.\n";
            case STEERING_SYSTEM -> "%s 조향장치를 선택하셨습니다.\n";
            case RUN_TEST -> throw new IllegalArgumentException("부품 선택 단계가 아닙니다.");
        };
        output.printf(format, component);
    }

    public void showInputError(String message) {
        output.println(message);
    }

    public void showGoodbye() {
        output.println("바이바이");
    }

    public void showTestStarted() {
        output.println("Test...");
    }

    public void showTestResult(Optional<String> error) {
        if (error.isPresent()) {
            output.println("자동차 부품 조합 테스트 결과 : FAIL");
            output.println(error.get());
        } else {
            output.println("자동차 부품 조합 테스트 결과 : PASS");
        }
    }

    public void showRunResult(Car car, RunStatus status) {
        switch (status) {
            case INCOMPATIBLE_PARTS -> output.println("자동차가 동작되지 않습니다");
            case BROKEN_ENGINE -> {
                output.println("엔진이 고장나있습니다.");
                output.println("자동차가 움직이지 않습니다.");
            }
            case RUNNING -> {
                output.printf("Car Type : %s\n", car.carType());
                output.printf("Engine   : %s\n", car.engine());
                output.printf("Brake    : %s\n", car.brakeSystem().runName());
                output.printf("Steering : %s\n", car.steeringSystem().runName());
                output.println("자동차가 동작됩니다.");
            }
        }
    }
}
