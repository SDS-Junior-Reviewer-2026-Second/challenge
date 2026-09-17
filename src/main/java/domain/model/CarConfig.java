package domain.model;

public class CarConfig {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brake;
    private SteeringSystem steering;

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public BrakeSystem getBrake() {
        return brake;
    }

    public void setBrake(BrakeSystem brake) {
        this.brake = brake;
    }

    public SteeringSystem getSteering() {
        return steering;
    }

    public void setSteering(SteeringSystem steering) {
        this.steering = steering;
    }
}
