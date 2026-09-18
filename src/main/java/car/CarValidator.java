package car;

import parts.BrakeSystem;
import parts.CarType;
import parts.Engine;
import parts.SteeringSystem;

final class CarValidator {
    private CarValidator() {
    }

    static String validationError(Car car) {
        if (car.getCarType() == CarType.SEDAN
                && car.getBrakeSystem() == BrakeSystem.CONTINENTAL) {
            return "Sedan에는 Continental제동장치 사용 불가";
        }

        if (car.getCarType() == CarType.SUV
                && car.getEngine() == Engine.TOYOTA) {
            return "SUV에는 TOYOTA엔진 사용 불가";
        }

        if (car.getCarType() == CarType.TRUCK
                && car.getEngine() == Engine.WIA) {
            return "Truck에는 WIA엔진 사용 불가";
        }

        if (car.getCarType() == CarType.TRUCK
                && car.getBrakeSystem() == BrakeSystem.MANDO) {
            return "Truck에는 Mando제동장치 사용 불가";
        }

        if (car.getBrakeSystem() == BrakeSystem.BOSCH
                && car.getSteeringSystem() != SteeringSystem.BOSCH) {
            return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";
        }

        return null;
    }
}
