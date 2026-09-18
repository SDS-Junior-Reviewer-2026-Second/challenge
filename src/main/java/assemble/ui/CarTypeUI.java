package assemble.ui;

import car.Car;
import parts.CarType;
import parts.SelectablePart;

public class CarTypeUI implements PartUI {
    @Override
    public void showMenu() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        for (CarType carType : CarType.values()) {
            System.out.printf("%d. %s%n", carType.getCode(), carType.getDisplayName());
        }
        System.out.println("===============================");
    }

    @Override
    public boolean isValidInput(int input) {
        return CarType.isValidCode(input);
    }

    @Override
    public void applySelection(Car car, int input) {
        car.selectCarType(input);
        System.out.printf("차량 타입으로 %s을 선택하셨습니다.%n", car.getCarType().getDisplayName());
    }

    @Override
    public String validationError() {
        return "ERROR :: 차량 타입은 " + SelectablePart.codeRange(CarType.values()) + " 범위만 선택 가능";
    }
}
