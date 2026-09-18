package menu;

import car.Car;
import car.SteeringSystem;
import service.CarService;
import state.Step;

public class SteeringSystemMenu implements Menu{
    @Override
    public void show() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. BOSCH");
        System.out.println("2. MOBIS");
        System.out.println("===============================");
    }

    @Override
    public boolean isValid(int input) {
        if (input < 0 || input > 2) {
            System.out.println("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
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

        SteeringSystem steering = SteeringSystem.from(input);
        car.selectSteeringSystem(steering);
        System.out.printf( "%s 조향장치를 선택하셨습니다.%n", steering.getDisplayName() );

        return Step.RUN_TEST;
    }
}
