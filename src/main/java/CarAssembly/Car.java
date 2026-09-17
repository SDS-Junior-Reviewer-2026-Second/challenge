package CarAssembly;

import CarEnums.BrakeSystem;
import CarEnums.CarType;
import CarEnums.Engine;
import CarEnums.SteeringSystem;

public class Car {
    private final CarType carType;
    private final Engine engine;
    private final BrakeSystem brakeSystem;
    private final SteeringSystem steeringSystem;

    public Car(CarType carType, Engine engine, BrakeSystem brakeSystem, SteeringSystem steeringSystem) {
        this.carType = carType;
        this.engine = engine;
        this.brakeSystem = brakeSystem;
        this.steeringSystem = steeringSystem;
    }

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

    public boolean hasBrokenEngine() {
        return engine.isBroken();
    }

    public String describe() {
        return "Car Type : " + carType.getLabel() + "\n"
                + "Engine   : " + engine.getLabel() + "\n"
                + "Brake    : " + brakeSystem.getLabel() + "\n"
                + "Steering : " + steeringSystem.getLabel();
    }
}