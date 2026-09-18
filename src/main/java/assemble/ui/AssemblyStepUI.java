package assemble.ui;

import car.Car;

public interface AssemblyStepUI {
    void showMenu();
    boolean isValidInput(int input);
    String validationError();
    StepAction execute(Car car, int input);
}
