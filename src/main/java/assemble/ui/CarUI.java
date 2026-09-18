package assemble.ui;

import car.Car;
import car.CarRunResult;

public class CarUI {
    public void runProducedCar(Car car) {
        CarRunResult result = car.run();

        if (result == CarRunResult.INVALID_COMBINATION) {
            System.out.println("자동차가 동작되지 않습니다");
            return;
        }
        if (result == CarRunResult.BROKEN_ENGINE) {
            System.out.println("엔진이 고장나있습니다.");
            System.out.println("자동차가 움직이지 않습니다.");
            return;
        }

        System.out.printf("Car Type : %s%n", car.getCarType().getDisplayName());
        System.out.printf("Engine   : %s%n", car.getEngine().getDisplayName());
        System.out.printf("Brake    : %s%n", car.getBrakeSystem().getDisplayName());
        System.out.printf("Steering : %s%n", car.getSteeringSystem().getDisplayName());
        System.out.println("자동차가 동작됩니다.");
    }

    public void testProducedCar(Car car) {
        String error = car.validationError();
        if (error == null) {
            System.out.println("자동차 부품 조합 테스트 결과 : PASS");
            return;
        }

        System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
        System.out.println(error);
    }
}
