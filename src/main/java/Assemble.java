import car.*;
import menu.Menu;
import menu.MenuFactory;
import service.CarService;
import validation.CarValidator;
import state.Step;

import java.util.Scanner;

public class Assemble {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Car car = new Car();
        MenuFactory menuFactory = new MenuFactory();
        CarValidator validator = new CarValidator();
        CarService carService = new CarService(validator);

        Step step = Step.CAR_TYPE;

        while (true) {
            clearScreen();
            Menu menu = menuFactory.create(step);
            menu.show();
            System.out.print("INPUT > ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("바이바이");
                break;
            }
            Integer answer = parseNumber(input);
            if (answer == null) {
                continue;
            }
            if (!menu.isValid(answer)) {
                delay(800);
                continue;
            }
            if (answer == 0) {
                if (step == Step.RUN_TEST) {
                    step = Step.CAR_TYPE;
                } else {
                    step = step.prev();
                }
                continue;
            }
            step = menu.execute(answer, car, carService);
            delay(800);
        }
        sc.close();
    }

    private static Integer parseNumber( String input ) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(  "ERROR :: 숫자만 입력 가능" );
            delay(800);
            return null;
        }
    }

    private static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

}