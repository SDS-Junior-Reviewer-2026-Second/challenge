package menu;

import state.Step;

public class MenuFactory {

    public Menu create(Step step) {

        return switch (step) {
            case CAR_TYPE -> new CarTypeMenu();
            case ENGINE -> new EngineMenu();
            case BRAKE_SYSTEM -> new BrakeSystemMenu();
            case STEERING_SYSTEM -> new SteeringSystemMenu();
            case RUN_TEST -> new RunTestMenu();
        };
    }
}