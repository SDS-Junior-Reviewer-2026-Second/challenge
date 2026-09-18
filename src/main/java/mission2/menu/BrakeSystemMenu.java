package mission2.menu;

import mission2.Car;

public class BrakeSystemMenu implements Menu {
    public void showMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");

        for (int i = 1; i < Car.brakeList.length; i++) {
            System.out.println(i + " " + Car.brakeList[i]);
        }
    }

    public boolean isValidInputRange(int answer) {
        if (answer < 0 || answer >= Car.brakeList.length) {
            System.out.println("ERROR :: 제동장치는 1 ~ " + (Car.brakeList.length - 1) + " 범위만 선택 가능");
            return false;
        }
        return true;
    }

    public Menu applyAndGetNextMenu(Car car, int choice) {
        if(choice == 0) return new EngineMenu();

        car.setBrake(Car.brakeList[choice]);
        System.out.printf("%s 제동장치를 선택하셨습니다.\n", car.getBrake());

        return new SteeringSystemMenu();
    }
}