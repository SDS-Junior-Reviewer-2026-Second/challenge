package service;

import car.Car;
import validation.CarValidator;
import validation.ValidationResult;

public class CarService {

    private final CarValidator validator;

    public CarService(CarValidator validator) {
        this.validator = validator;
    }

    public void run(Car car) {

        ValidationResult result = validator.validate(car);

        if (!result.isValid()) {
            System.out.println("자동차가 동작되지 않습니다");
            return;
        }

        if (car.getEngine().isBroken()) {
            System.out.println("엔진이 고장나있습니다.");
            System.out.println("자동차가 움직이지 않습니다.");
            return;
        }

        printSpecification(car);

        System.out.println("자동차가 동작됩니다.");
    }

    public void test(Car car) {

        ValidationResult result = validator.validate(car);

        if (!result.isValid()) {
            System.out.println(
                    "자동차 부품 조합 테스트 결과 : FAIL"
            );

            System.out.println(result.getMessage());
            return;
        }

        System.out.println(
                "자동차 부품 조합 테스트 결과 : PASS"
        );
    }

    private void printSpecification(Car car) {

        System.out.printf(
                "Car Type : %s%n",
                car.getType().getDisplayName()
        );

        System.out.printf(
                "Engine   : %s%n",
                car.getEngine().getDisplayName()
        );

        System.out.printf(
                "Brake    : %s%n",
                car.getBrakeSystem().getDisplayName()
        );

        System.out.printf(
                "Steering : %s%n",
                car.getSteeringSystem().getDisplayName()
        );
    }
}