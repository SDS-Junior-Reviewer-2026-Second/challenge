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
        return CarValidator.validationError(this);
    }
}
