package org.example.assemble;

import java.util.Objects;

public record VehicleConfiguration(
        CarType carType,
        Engine engine,
        BrakeSystem brakeSystem,
        SteeringSystem steeringSystem
) {
    public VehicleConfiguration {
        Objects.requireNonNull(carType, "carType");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(brakeSystem, "brakeSystem");
        Objects.requireNonNull(steeringSystem, "steeringSystem");
    }
}
