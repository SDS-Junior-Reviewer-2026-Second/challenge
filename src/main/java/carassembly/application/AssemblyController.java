package carassembly.application;

import carassembly.console.ConsoleView;
import carassembly.domain.BrakeSystem;
import carassembly.domain.Car;
import carassembly.domain.CarType;
import carassembly.domain.Engine;
import carassembly.domain.SteeringSystem;

import java.util.Objects;
import java.util.Optional;
import java.util.function.IntConsumer;

/** 입력을 해석하고, 부품을 선택하고, 단계와 실행 순서를 관리한다. */
public final class AssemblyController {
    private static final int SELECTION_DELAY_MS = 800;
    private static final int TEST_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    private final ConsoleView view;
    private final IntConsumer pause;

    private AssemblyStep step = AssemblyStep.CAR_TYPE;
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public AssemblyController(ConsoleView view, IntConsumer pause) {
        this.view = Objects.requireNonNull(view);
        this.pause = Objects.requireNonNull(pause);
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            view.showMenu(step);
            Optional<String> command = view.readCommand();
            if (command.isEmpty()) {
                return;
            }
            if (command.get().equalsIgnoreCase("exit")) {
                view.showGoodbye();
                return;
            }

            Integer answer = parseAnswer(command.get());
            if (answer == null) {
                continue;
            }
            if (answer == 0) {
                step = step.previous();
            } else if (step == AssemblyStep.RUN_TEST) {
                executeAction(answer);
            } else {
                selectComponent(answer);
                pause.accept(SELECTION_DELAY_MS);
                step = step.next();
            }
        }
    }

    private Integer parseAnswer(String command) {
        final int answer;
        try {
            answer = Integer.parseInt(command);
        } catch (NumberFormatException e) {
            showInputError("ERROR :: 숫자만 입력 가능");
            return null;
        }
        if (!step.accepts(answer)) {
            showInputError(step.errorMessage());
            return null;
        }
        return answer;
    }

    private void showInputError(String message) {
        view.showInputError(message);
        pause.accept(SELECTION_DELAY_MS);
    }

    private void selectComponent(int answer) {
        // 화면도 같은 enum 순서로 만들어져 메뉴 번호와 선택 부품이 일치한다.
        int index = answer - 1;
        Object component = switch (step) {
            case CAR_TYPE -> carType = CarType.values()[index];
            case ENGINE -> engine = Engine.values()[index];
            case BRAKE_SYSTEM -> brakeSystem = BrakeSystem.values()[index];
            case STEERING_SYSTEM -> steeringSystem = SteeringSystem.values()[index];
            case RUN_TEST -> throw new IllegalStateException("부품 선택 단계가 아닙니다.");
        };
        view.showSelection(step, component);
    }

    private void executeAction(int answer) {
        Car car = new Car(carType, engine, brakeSystem, steeringSystem);
        if (answer == 1) {
            view.showRunResult(car, car.runStatus());
        } else {
            view.showTestStarted();
            pause.accept(TEST_DELAY_MS);
            view.showTestResult(car.findCompatibilityError());
        }
        pause.accept(RESULT_DELAY_MS);
    }
}
