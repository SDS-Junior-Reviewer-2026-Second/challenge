package assemble.ui;

import car.Car;

public interface PartUI extends AssemblyStepUI {
    void applySelection(Car car, int input);

    @Override
    default StepAction execute(Car car, int input) {
        applySelection(car, input);
        return StepAction.NEXT;
    }
}
