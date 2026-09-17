public record Car(
    CarType carType,
    EngineType engineType,
    BrakeType brakeType,
    SteeringType steeringType
) {
    public boolean hasComponent(ComponentType type) {
        return (
            carType == type ||
            engineType == type ||
            brakeType == type ||
            steeringType == type
        );
    }
}
