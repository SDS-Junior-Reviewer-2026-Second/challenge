package mission2.menu;

import mission2.Car;

public class CarTypeMenu implements Menu {
    public void showMenu() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");

        for (int i = 1; i < Car.carTypeList.length; i++) {
            System.out.println(i + " " + Car.carTypeList[i]);
        }
    }

    public boolean isValidInputRange(int answer) {
        if (answer < 1 || answer >= Car.carTypeList.length) {
            System.out.println("ERROR :: 차량 타입은 1 ~ " + (Car.carTypeList.length-1) + " 범위만 선택 가능");
            return false;
        }
        return true;
    }

    public Menu applyAndGetNextMenu(Car car, int choice) {
        car.setCarType(Car.carTypeList[choice]);
        System.out.printf("%s 엔진을 선택하셨습니다.\n", car.getCarType());
        return new EngineMenu();
    }
}
