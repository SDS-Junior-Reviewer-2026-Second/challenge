import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Business rules and operations available after assembly is complete. */
final class CarService {
    List<String> run(CarConfiguration car) {
        if (findCompatibilityError(car).isPresent()) {
            return Collections.singletonList("자동차가 동작되지 않습니다");
        }

        if (car.engine() == Engine.BROKEN) {
            return Arrays.asList(
                    "엔진이 고장나있습니다.",
                    "자동차가 움직이지 않습니다.");
        }

        return Arrays.asList(
                "Car Type : " + car.carType().displayName(),
                "Engine   : " + car.engine().displayName(),
                "Brake    : " + car.brakeSystem().runName(),
                "Steering : " + car.steeringSystem().runName(),
                "자동차가 동작됩니다.");
    }

    List<String> test(CarConfiguration car) {
        Optional<String> error = findCompatibilityError(car);
        if (error.isPresent()) {
            return Arrays.asList(
                    "자동차 부품 조합 테스트 결과 : FAIL",
                    error.get());
        }

        return Collections.singletonList("자동차 부품 조합 테스트 결과 : PASS");
    }

    private Optional<String> findCompatibilityError(CarConfiguration car) {
        if (car.carType() == CarType.SEDAN && car.brakeSystem() == BrakeSystem.CONTINENTAL) {
            return Optional.of("Sedan에는 Continental제동장치 사용 불가");
        }
        if (car.carType() == CarType.SUV && car.engine() == Engine.TOYOTA) {
            return Optional.of("SUV에는 TOYOTA엔진 사용 불가");
        }
        if (car.carType() == CarType.TRUCK && car.engine() == Engine.WIA) {
            return Optional.of("Truck에는 WIA엔진 사용 불가");
        }
        if (car.carType() == CarType.TRUCK && car.brakeSystem() == BrakeSystem.MANDO) {
            return Optional.of("Truck에는 Mando제동장치 사용 불가");
        }
        if (car.brakeSystem() == BrakeSystem.BOSCH && car.steeringSystem() != SteeringSystem.BOSCH) {
            return Optional.of("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
        }
        return Optional.empty();
    }
}
