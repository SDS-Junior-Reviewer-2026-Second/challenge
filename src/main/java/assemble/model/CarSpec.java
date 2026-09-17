package assemble.model;

/** 조립 단계별 선택값. 아직 고르지 않은 항목은 null. */
public record CarSpec(CarType carType, Engine engine, BrakeSystem brake, SteeringSystem steering) {

    public static CarSpec empty() {
        return new CarSpec(null, null, null, null);
    }

    public CarSpec withCarType(CarType carType) {
        return new CarSpec(carType, engine, brake, steering);
    }

    public CarSpec withEngine(Engine engine) {
        return new CarSpec(carType, engine, brake, steering);
    }

    public CarSpec withBrake(BrakeSystem brake) {
        return new CarSpec(carType, engine, brake, steering);
    }

    public CarSpec withSteering(SteeringSystem steering) {
        return new CarSpec(carType, engine, brake, steering);
    }
}
