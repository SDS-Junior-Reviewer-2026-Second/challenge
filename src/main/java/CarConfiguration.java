/** Current choices made by the user while assembling a car. */
final class CarConfiguration {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    void selectCarType(CarType carType) {
        this.carType = carType;
    }

    void selectEngine(Engine engine) {
        this.engine = engine;
    }

    void selectBrakeSystem(BrakeSystem brakeSystem) {
        this.brakeSystem = brakeSystem;
    }

    void selectSteeringSystem(SteeringSystem steeringSystem) {
        this.steeringSystem = steeringSystem;
    }

    CarType carType() {
        return carType;
    }

    Engine engine() {
        return engine;
    }

    BrakeSystem brakeSystem() {
        return brakeSystem;
    }

    SteeringSystem steeringSystem() {
        return steeringSystem;
    }
}
