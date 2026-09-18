package mission2.menu;

import mission2.Car;

public class SteeringSystemMenu implements Menu {
    public void showMenu() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");

        for (int i = 1; i < Car.steeringList.length; i++) {
            System.out.println(i + " " + Car.steeringList[i]);
        }
    }

    public boolean isValidInputRange(int answer) {
        if (answer < 0 || answer >= Car.steeringList.length) {
            System.out.println("ERROR :: 조향장치는 1 ~ " + (Car.steeringList.length-1) +" 범위만 선택 가능");
            return false;
        }
        return true;
    }

    public Menu applyAndGetNextMenu(Car car, int choice) {
        if(choice == 0) return new BrakeSystemMenu();

        car.setSteering(Car.steeringList[choice]);
        System.out.printf("%s 조향장치를 선택하셨습니다.\n", car.getSteering());

        return new RunTestMenu();
    }
}
