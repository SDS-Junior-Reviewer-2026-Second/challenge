package validation;

import car.*;

public class CarValidator {

    public ValidationResult validate(Car car) {

        if (
                car.getType() == CarType.SEDAN &&
                        car.getBrakeSystem() == BrakeSystem.CONTINENTAL
        ) {
            return ValidationResult.fail(
                    "Sedan에는 Continental 제동장치 사용 불가"
            );
        }

        if (
                car.getType() == CarType.SUV &&
                        car.getEngine() == Engine.TOYOTA
        ) {
            return ValidationResult.fail(
                    "SUV에는 TOYOTA 엔진 사용 불가"
            );
        }

        if (
                car.getType() == CarType.TRUCK &&
                        car.getEngine() == Engine.WIA
        ) {
            return ValidationResult.fail(
                    "Truck에는 WIA 엔진 사용 불가"
            );
        }

        if (
                car.getType() == CarType.TRUCK &&
                        car.getBrakeSystem() == BrakeSystem.MANDO
        ) {
            return ValidationResult.fail(
                    "Truck에는 Mando 제동장치 사용 불가"
            );
        }

        if (
                car.getBrakeSystem() == BrakeSystem.BOSCH &&
                        car.getSteeringSystem() != SteeringSystem.BOSCH
        ) {
            return ValidationResult.fail(
                    "Bosch 제동장치에는 Bosch 조향장치 이외 사용 불가"
            );
        }

        return ValidationResult.success();
    }
}
