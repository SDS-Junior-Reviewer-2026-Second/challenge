package mission2.menu;

import mission2.Car;

public class EngineMenu implements Menu {
    public void showMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");

        for (int i = 1; i < Car.engineList.length; i++) {
            System.out.println(i + " " + Car.engineList[i]);
        }
    }

    public boolean isValidInputRange(int answer) {
        if (answer < 0 || answer >= Car.engineList.length) {
            System.out.println("ERROR :: 엔진은 1 ~ " + (Car.engineList.length-1) +"범위만 선택 가능");
            return false;
        }
        return true;
    }

    public Menu applyAndGetNextMenu(Car car, int choice) {
        if(choice == 0) return new CarTypeMenu();

        car.setEngine(Car.engineList[choice]);
        System.out.printf("%s 엔진을 선택하셨습니다.\n", car.getEngine());

        return new BrakeSystemMenu();
    }

}