import java.util.Objects;

/** 조립이 끝난 완성 차량. 생성된 뒤에는 부품 구성이 바뀌지 않는다. */
record Car(CarType carType, Engine engine,
           BrakeSystem brakeSystem, SteeringSystem steeringSystem) {

    Car {
        Objects.requireNonNull(carType, "carType");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(brakeSystem, "brakeSystem");
        Objects.requireNonNull(steeringSystem, "steeringSystem");
    }
}
