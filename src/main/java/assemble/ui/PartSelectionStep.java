package assemble.ui;

import car.Car;

public interface PartSelectionStep {
    void showMenu();

    boolean isValidInput(int input);

    void applySelection(Car car, int input);

    String validationError();
}
