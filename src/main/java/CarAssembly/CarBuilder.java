package CarAssembly;

import CarEnums.BrakeSystem;
import CarEnums.CarType;
import CarEnums.Engine;
import CarEnums.SteeringSystem;

public class CarBuilder {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public void carType(CarType carType) {
        this.carType = carType;
    }

    public void engine(Engine engine) {
        this.engine = engine;
    }

    public void brakeSystem(BrakeSystem brakeSystem) {
        this.brakeSystem = brakeSystem;
    }

    public void steeringSystem(SteeringSystem steeringSystem) {
        this.steeringSystem = steeringSystem;
    }

    public Car build() {
        return new Car(carType, engine, brakeSystem, steeringSystem);
    }
}