package CarEnums;

import CarAssembly.Car;

public enum CarCompatibilityRule {

    SEDAN_CANNOT_USE_CONTINENTAL_BRAKE("Sedan에는 Continental제동장치 사용 불가") {
        @Override
        public boolean isViolatedBy(Car car) {
            return car.getCarType() == CarType.SEDAN
                    && car.getBrakeSystem() == BrakeSystem.CONTINENTAL;
        }
    },
    SUV_CANNOT_USE_TOYOTA_ENGINE("SUV에는 TOYOTA엔진 사용 불가") {
        @Override
        public boolean isViolatedBy(Car car) {
            return car.getCarType() == CarType.SUV
                    && car.getEngine() == Engine.TOYOTA;
        }
    },
    TRUCK_CANNOT_USE_WIA_ENGINE("Truck에는 WIA엔진 사용 불가") {
        @Override
        public boolean isViolatedBy(Car car) {
            return car.getCarType() == CarType.TRUCK
                    && car.getEngine() == Engine.WIA;
        }
    },
    TRUCK_CANNOT_USE_MANDO_BRAKE("Truck에는 Mando제동장치 사용 불가") {
        @Override
        public boolean isViolatedBy(Car car) {
            return car.getCarType() == CarType.TRUCK
                    && car.getBrakeSystem() == BrakeSystem.MANDO;
        }
    },
    BOSCH_BRAKE_REQUIRES_BOSCH_STEERING("Bosch제동장치에는 Bosch조향장치 이외 사용 불가") {
        @Override
        public boolean isViolatedBy(Car car) {
            return car.getBrakeSystem() == BrakeSystem.BOSCH
                    && car.getSteeringSystem() != SteeringSystem.BOSCH;
        }
    };

    private final String message;

    CarCompatibilityRule(String message) {
        this.message = message;
    }

    public abstract boolean isViolatedBy(Car car);

    public String violationMessage() {
        return message;
    }
}