package menu;

import car.BrakeSystem;
import car.Car;
import service.CarService;
import state.Step;

public class BrakeSystemMenu implements Menu {

    @Override
    public void show() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. MANDO");
        System.out.println("2. CONTINENTAL");
        System.out.println("3. BOSCH");
        System.out.println("===============================");
    }

    @Override
    public boolean isValid(int input) {
        if (input < 0 || input > 3) {
            System.out.println("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
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

        BrakeSystem brake = BrakeSystem.from(input);
        car.selectBrakeSystem(brake);
        System.out.printf( "%s 제동장치를 선택하셨습니다.%n", brake.getDisplayName());

        return Step.STEERING_SYSTEM;
    }
}
