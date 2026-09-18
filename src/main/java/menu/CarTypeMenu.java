package menu;

import car.Car;
import car.CarType;
import service.CarService;
import state.Step;

public class CarTypeMenu implements Menu {

    @Override
    public void show() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        System.out.println("1. Sedan");
        System.out.println("2. SUV");
        System.out.println("3. Truck");
        System.out.println("===============================");
    }

    @Override
    public boolean isValid(int input) {
        if (input < 1 || input > 3) {
            System.out.println(
                    "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능"
            );
            return false;
        }

        return true;
    }

    @Override
    public Step execute(
            int input,
            Car car,
            CarService carService
    ) {

        CarType type = CarType.from(input);

        car.selectCarType(type);

        System.out.printf(
                "차량 타입으로 %s을 선택하셨습니다.%n",
                type.getDisplayName()
        );

        return Step.ENGINE;
    }
}