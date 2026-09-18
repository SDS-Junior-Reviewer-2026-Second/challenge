import java.util.Objects;
import java.util.Optional;
import java.util.function.IntConsumer;

final class AssemblyProgram {
    private static final int SELECTION_DELAY_MS = 800;
    private static final int TEST_START_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    private final ConsoleView view;
    private final CarService carService;
    private final IntConsumer delay;
    private final CarConfiguration car = new CarConfiguration();

    private AssemblyStep step = AssemblyStep.CAR_TYPE;

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
        CarType selected = CarType.fromCode(answer);
        car.selectCarType(selected);
        view.showCarTypeSelection(selected);
        advanceTo(AssemblyStep.ENGINE);
    }

    private void selectEngine(int answer) {
        Engine selected = Engine.fromCode(answer);
        car.selectEngine(selected);
        view.showEngineSelection(selected);
        advanceTo(AssemblyStep.BRAKE_SYSTEM);
    }

    private void selectBrakeSystem(int answer) {
        BrakeSystem selected = BrakeSystem.fromCode(answer);
        car.selectBrakeSystem(selected);
        view.showBrakeSelection(selected);
        advanceTo(AssemblyStep.STEERING_SYSTEM);
    }

    private void selectSteeringSystem(int answer) {
        SteeringSystem selected = SteeringSystem.fromCode(answer);
        car.selectSteeringSystem(selected);
        view.showSteeringSelection(selected);
        advanceTo(AssemblyStep.RUN_TEST);
    }

    private void advanceTo(AssemblyStep nextStep) {
        delay.accept(SELECTION_DELAY_MS);
        step = nextStep;
    }

    private void processCompletedCar(int answer) {
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
}
