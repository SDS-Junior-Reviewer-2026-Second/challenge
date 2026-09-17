package org.example.assemble;

import java.util.Objects;

public final class AssemblyApplication {
    private static final int SELECTION_DELAY_MS = 800;
    private static final int TEST_START_DELAY_MS = 1_500;
    private static final int RESULT_DELAY_MS = 2_000;

    private final UserInterface userInterface;
    private final CompatibilityPolicy compatibilityPolicy;
    private final Delay delay;

    private AssemblyStep step = AssemblyStep.CAR_TYPE;
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public AssemblyApplication(
            UserInterface userInterface,
            CompatibilityPolicy compatibilityPolicy,
            Delay delay
    ) {
        this.userInterface = Objects.requireNonNull(userInterface, "userInterface");
        this.compatibilityPolicy = Objects.requireNonNull(compatibilityPolicy, "compatibilityPolicy");
        this.delay = Objects.requireNonNull(delay, "delay");
    }

    public void run() {
        while (true) {
            userInterface.showMenu(step);
            String input = userInterface.readInput().trim();

            if (input.equalsIgnoreCase("exit")) {
                userInterface.showGoodbye();
                return;
            }

            Integer choice = parseChoice(input);
            if (choice == null) {
                continue;
            }
            if (!step.accepts(choice)) {
                userInterface.showRangeError(step);
                delay.pause(SELECTION_DELAY_MS);
                continue;
            }
            if (choice == 0) {
                step = step.previous();
                continue;
            }

            applyChoice(choice);
        }
    }

    private Integer parseChoice(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            userInterface.showNumberRequired();
            delay.pause(SELECTION_DELAY_MS);
            return null;
        }
    }

    private void applyChoice(int choice) {
        switch (step) {
            case CAR_TYPE -> selectCarType(choice);
            case ENGINE -> selectEngine(choice);
            case BRAKE_SYSTEM -> selectBrakeSystem(choice);
            case STEERING_SYSTEM -> selectSteeringSystem(choice);
            case RUN_TEST -> performAction(choice);
        }
    }

    private void selectCarType(int choice) {
        carType = CarType.fromChoice(choice);
        userInterface.showSelection(carType);
        delay.pause(SELECTION_DELAY_MS);
        step = AssemblyStep.ENGINE;
    }

    private void selectEngine(int choice) {
        engine = Engine.fromChoice(choice);
        userInterface.showSelection(engine);
        delay.pause(SELECTION_DELAY_MS);
        step = AssemblyStep.BRAKE_SYSTEM;
    }

    private void selectBrakeSystem(int choice) {
        brakeSystem = BrakeSystem.fromChoice(choice);
        userInterface.showSelection(brakeSystem);
        delay.pause(SELECTION_DELAY_MS);
        step = AssemblyStep.STEERING_SYSTEM;
    }

    private void selectSteeringSystem(int choice) {
        steeringSystem = SteeringSystem.fromChoice(choice);
        userInterface.showSelection(steeringSystem);
        delay.pause(SELECTION_DELAY_MS);
        step = AssemblyStep.RUN_TEST;
    }

    private void performAction(int choice) {
        VehicleConfiguration configuration = currentConfiguration();
        if (choice == 1) {
            runProducedCar(configuration);
            delay.pause(RESULT_DELAY_MS);
            return;
        }

        userInterface.showTestStarted();
        delay.pause(TEST_START_DELAY_MS);
        userInterface.showTestResult(compatibilityPolicy.evaluate(configuration));
        delay.pause(RESULT_DELAY_MS);
    }

    private void runProducedCar(VehicleConfiguration configuration) {
        CompatibilityResult result = compatibilityPolicy.evaluate(configuration);
        if (!result.valid()) {
            userInterface.showInvalidCar();
            return;
        }
        if (configuration.engine() == Engine.BROKEN) {
            userInterface.showBrokenEngine();
            return;
        }
        userInterface.showRunningCar(configuration);
    }

    private VehicleConfiguration currentConfiguration() {
        return new VehicleConfiguration(carType, engine, brakeSystem, steeringSystem);
    }
}
