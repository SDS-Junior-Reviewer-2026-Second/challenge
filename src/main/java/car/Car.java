package car;

public class Car {

    private CarType type;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public void selectCarType(CarType type) {
        this.type = type;
    }

    public void selectEngine(Engine engine) {
        this.engine = engine;
    }

    public void selectBrakeSystem(BrakeSystem brakeSystem) {
        this.brakeSystem = brakeSystem;
    }

    public void selectSteeringSystem(SteeringSystem steeringSystem) {
        this.steeringSystem = steeringSystem;
    }

    public CarType getType() {
        return type;
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
}