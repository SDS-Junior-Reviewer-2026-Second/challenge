package menu;

import car.Car;
import service.CarService;
import state.Step;

public interface Menu {
    void show();
    boolean isValid(int input);
    Step execute(int input, Car car, CarService carService);
}
