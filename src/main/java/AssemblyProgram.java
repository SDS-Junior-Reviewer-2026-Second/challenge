import java.util.Objects;
import java.util.Optional;
import java.util.function.IntConsumer;

/** 콘솔 조립 과정의 흐름과 현재 선택 상태를 관리한다. */
final class AssemblyProgram {
    private static final int SELECTION_DELAY_MS = 800;
    private static final int TEST_START_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    private final ConsoleView view;
    private final CarService carService;
    private final IntConsumer delay;

    private AssemblyStep step = AssemblyStep.CAR_TYPE;
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    AssemblyProgram(ConsoleView view, CarService carService, IntConsumer delay) {
        this.view = Objects.requireNonNull(view);
        this.carService = Objects.requireNonNull(carService);
        this.delay = Objects.requireNonNull(delay);
    }

    void run() {
        while (true) {
            view.showMenu(step);

            String input = view.readInput();
            if ("exit".equalsIgnoreCase(input)) {
                view.showGoodbye();
                return;
            }

            Integer answer = parseInteger(input);
            if (answer == null) {
                showInputError("ERROR :: 숫자만 입력 가능");
                continue;
            }

            if (!step.accepts(answer)) {
                showInputError(step.errorMessage());
                continue;
            }

            if (answer == 0) {
                step = step.backStep();
                continue;
            }

            process(answer);
        }
    }

    private Integer parseInteger(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showInputError(String message) {
        view.showInputError(message);
        delay.accept(SELECTION_DELAY_MS);
    }

    private void process(int answer) {
        switch (step) {
            case CAR_TYPE -> selectCarType(answer);
            case ENGINE -> selectEngine(answer);
            case BRAKE_SYSTEM -> selectBrakeSystem(answer);
            case STEERING_SYSTEM -> selectSteeringSystem(answer);
            case RUN_TEST -> processCompletedCar(answer);
        }
    }

    private void selectCarType(int answer) {
        carType = CarType.fromCode(answer);
        view.showCarTypeSelection(carType);
        advance();
    }

    private void selectEngine(int answer) {
        engine = Engine.fromCode(answer);
        view.showEngineSelection(engine);
        advance();
    }

    private void selectBrakeSystem(int answer) {
        brakeSystem = BrakeSystem.fromCode(answer);
        view.showBrakeSelection(brakeSystem);
        advance();
    }

    private void selectSteeringSystem(int answer) {
        steeringSystem = SteeringSystem.fromCode(answer);
        view.showSteeringSelection(steeringSystem);
        advance();
    }

    private void advance() {
        delay.accept(SELECTION_DELAY_MS);
        step = step.nextStep();
    }

    private void processCompletedCar(int answer) {
        Car car = completedCar();

        if (answer == 1) {
            view.showRunResult(car, carService.run(car));
            delay.accept(RESULT_DELAY_MS);
            return;
        }

        view.showTestStarted();
        delay.accept(TEST_START_DELAY_MS);
        Optional<String> failure = carService.test(car);
        view.showTestResult(failure);
        delay.accept(RESULT_DELAY_MS);
    }

    private Car completedCar() {
        return new Car(carType, engine, brakeSystem, steeringSystem);
    }
}
