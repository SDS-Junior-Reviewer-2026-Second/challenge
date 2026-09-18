package mission2.display;

import mission2.car.Car;

import java.awt.*;

public class Display {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    public void displayStep(Car car) {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();

        int step = car.getStep();
        MenuCommand menu = new MenuCommand(step);
        menu.command();

        System.out.print("INPUT > ");
    }
}
