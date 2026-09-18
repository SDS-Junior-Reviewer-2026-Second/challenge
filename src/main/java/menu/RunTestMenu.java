package menu;

import car.Car;
import service.CarService;
import state.Step;

public class RunTestMenu implements Menu{
    @Override
    public void show() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
        System.out.println("===============================");
    }

    @Override
    public boolean isValid(int input) {
        if (input < 0 || input > 2) {
            System.out.println("ERROR :: Run 또는 Test 중 하나를 선택 필요");
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

        if (input == 1) {
            carService.run(car);
        }

        if (input == 2) {
            carService.test(car);
        }

        return Step.RUN_TEST;
    }
}
