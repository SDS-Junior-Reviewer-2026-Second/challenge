package assemble;

import assemble.ui.AssemblyStepUI;
import assemble.ui.BrakeSystemUI;
import assemble.ui.CarTypeUI;
import assemble.ui.EngineUI;
import assemble.ui.RunTestUI;
import assemble.ui.SteeringSystemUI;
import assemble.ui.StepAction;
import car.Car;

public enum AssemblyStep {
    CAR_TYPE(new CarTypeUI(), BackNavigation.PREVIOUS),
    ENGINE(new EngineUI(), BackNavigation.PREVIOUS),
    BRAKE_SYSTEM(new BrakeSystemUI(), BackNavigation.PREVIOUS),
    STEERING_SYSTEM(new SteeringSystemUI(), BackNavigation.PREVIOUS),
    RUN_TEST(new RunTestUI(), BackNavigation.FIRST);

    public static final int BACK = 0;
    public static final int RUN = 1;
    public static final int TEST = 2;

    private final AssemblyStepUI ui;
    private final BackNavigation backNavigation;

    AssemblyStep(AssemblyStepUI ui, BackNavigation backNavigation) {
        this.ui = ui;
        this.backNavigation = backNavigation;
    }

    public void showMenu() {
        ui.showMenu();
    }

    public boolean isValidInput(int input) {
        return ui.isValidInput(input);
    }

    public String validationError() {
        return ui.validationError();
    }

    public StepAction execute(Car car, int input) {
        return ui.execute(car, input);
    }

    public BackNavigation getBackNavigation() {
        return backNavigation;
    }
}
