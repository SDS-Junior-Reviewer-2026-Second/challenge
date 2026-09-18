package menu;

import car.Car;
import car.Engine;
import service.CarService;
import state.Step;

public class EngineMenu implements Menu {

    @Override
    public void show() {
        System.out.println("어떤 엔진을 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. GM");
        System.out.println("2. TOYOTA");
        System.out.println("3. WIA");
        System.out.println("4. 고장난 엔진");
    }

    @Override
    public boolean isValid(int input) {
        if (input < 0 || input > 4) {
            System.out.println(
                    "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능"
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

        Engine engine = Engine.from(input);

        car.selectEngine(engine);

        System.out.printf(
                "%s 엔진을 선택하셨습니다.%n",
                engine.getDisplayName()
        );

        return Step.BRAKE_SYSTEM;
    }
}