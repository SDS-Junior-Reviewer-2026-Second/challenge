package car;

import parts.BrakeSystem;
import parts.CarType;
import parts.Engine;
import parts.SteeringSystem;

public class Car {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public void selectCarType(int code) {
        carType = CarType.fromCode(code);
    }

    public void selectEngine(int code) {
        engine = Engine.fromCode(code);
    }

    public void selectBrakeSystem(int code) {
        brakeSystem = BrakeSystem.fromCode(code);
    }

    public void selectSteeringSystem(int code) {steeringSystem = SteeringSystem.fromCode(code);}

    public CarType getCarType() {
        return carType;
    }

    public Engine getEngine() {
        return engine;
    }

    public BrakeSystem getBrakeSystem() {
        return brakeSystem;
    }

    public SteeringSystem getSteeringSystem() {
        return steeringSystem;
    }

    public boolean isValid() {
        return validationError() == null;
    }

    public CarRunResult run() {
        if (!isValid()) {
            return CarRunResult.INVALID_COMBINATION;
        }
        if (engine.isBroken()) {
            return CarRunResult.BROKEN_ENGINE;
        }
        return CarRunResult.SUCCESS;
    }

    public String validationError() {
        if (carType == CarType.SEDAN && brakeSystem == BrakeSystem.CONTINENTAL) {
            return "Sedan에는 Continental제동장치 사용 불가";
        }
        if (carType == CarType.SUV && engine == Engine.TOYOTA) {
            return "SUV에는 TOYOTA엔진 사용 불가";
        }
        if (carType == CarType.TRUCK && engine == Engine.WIA) {
            return "Truck에는 WIA엔진 사용 불가";
        }
        if (carType == CarType.TRUCK && brakeSystem == BrakeSystem.MANDO) {
            return "Truck에는 Mando제동장치 사용 불가";
        }
        if (brakeSystem == BrakeSystem.BOSCH && steeringSystem != SteeringSystem.BOSCH) {
            return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";
        }
        return null;
    }
}
