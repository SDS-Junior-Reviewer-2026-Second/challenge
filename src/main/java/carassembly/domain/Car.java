package carassembly.domain;

import java.util.Objects;
import java.util.Optional;

/** 조립 완료된 차량. 부품과 규칙만 관리하며 입력, 출력, 대기를 수행하지 않는다. */
public record Car(CarType carType, Engine engine,
                  BrakeSystem brakeSystem, SteeringSystem steeringSystem) {
    public Car {
        Objects.requireNonNull(carType, "carType");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(brakeSystem, "brakeSystem");
        Objects.requireNonNull(steeringSystem, "steeringSystem");
    }

    /** 복수 규칙 위반 시 원본과 동일하게 첫 번째 오류를 반환한다. */
    public Optional<String> findCompatibilityError() {
        if (carType == CarType.SEDAN && brakeSystem == BrakeSystem.CONTINENTAL) {
            return Optional.of("Sedan에는 Continental제동장치 사용 불가");
        }
        if (carType == CarType.SUV && engine == Engine.TOYOTA) {
            return Optional.of("SUV에는 TOYOTA엔진 사용 불가");
        }
        if (carType == CarType.TRUCK && engine == Engine.WIA) {
            return Optional.of("Truck에는 WIA엔진 사용 불가");
        }
        if (carType == CarType.TRUCK && brakeSystem == BrakeSystem.MANDO) {
            return Optional.of("Truck에는 Mando제동장치 사용 불가");
        }
        if (brakeSystem == BrakeSystem.BOSCH && steeringSystem != SteeringSystem.BOSCH) {
            return Optional.of("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
        }
        return Optional.empty();
    }

    public RunStatus runStatus() {
        if (findCompatibilityError().isPresent()) {
            return RunStatus.INCOMPATIBLE_PARTS;
        }
        if (engine == Engine.BROKEN) {
            return RunStatus.BROKEN_ENGINE;
        }
        return RunStatus.RUNNING;
    }
}
