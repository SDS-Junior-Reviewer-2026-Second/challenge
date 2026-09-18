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
        if (carType == null || engine == null || brakeSystem == null || steeringSystem == null) {
            throw new IllegalStateException("모든 부품(차량 타입/엔진/제동장치/조향장치)이 선택되어야 조립할 수 있습니다.");
        }
        return new Car(carType, engine, brakeSystem, steeringSystem);
    }
}