import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 원본은 isValidCheck()(불리언 반환용 if 체인 5개)와 testProducedCar()(메시지 출력용
 * if-else if 체인 5개)에 완전히 동일한 5가지 호환성 규칙을 두 번 따로 작성했다.
 * 규칙을 하나 고치거나 추가하려면 두 메서드를 동시에 손봐야 하는 구조였다.
 * 이제는 규칙(조건 + 실패 메시지)을 enum 상수 하나당 하나씩 정의하고,
 * firstViolation()이 "처음으로 위반된 규칙"을 찾아 Car.run()/Car.test() 둘 다에서
 * 재사용하도록 했다. enum 선언 순서 = 원본 if-else if의 우선순위와 동일하게 맞춰서,
 * 여러 규칙을 동시에 위반해도 원본과 같은 메시지가 나오게 했다.
 */
public enum CompatibilityRule {
    SEDAN_FORBIDS_CONTINENTAL_BRAKE(
            car -> car.getCarType() == CarType.SEDAN && car.getBrakeSystem() == BrakeSystem.CONTINENTAL,
            "Sedan에는 Continental제동장치 사용 불가"),
    SUV_FORBIDS_TOYOTA_ENGINE(
            car -> car.getCarType() == CarType.SUV && car.getEngine() == Engine.TOYOTA,
            "SUV에는 TOYOTA엔진 사용 불가"),
    TRUCK_FORBIDS_WIA_ENGINE(
            car -> car.getCarType() == CarType.TRUCK && car.getEngine() == Engine.WIA,
            "Truck에는 WIA엔진 사용 불가"),
    TRUCK_FORBIDS_MANDO_BRAKE(
            car -> car.getCarType() == CarType.TRUCK && car.getBrakeSystem() == BrakeSystem.MANDO,
            "Truck에는 Mando제동장치 사용 불가"),
    BOSCH_BRAKE_REQUIRES_BOSCH_STEERING(
            car -> car.getBrakeSystem() == BrakeSystem.BOSCH && car.getSteeringSystem() != SteeringSystem.BOSCH,
            "Bosch제동장치에는 Bosch조향장치 이외 사용 불가");

    private final Predicate<Car> violated;
    private final String message;

    CompatibilityRule(Predicate<Car> violated, String message) {
        this.violated = violated;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static Optional<CompatibilityRule> firstViolation(Car car) {
        return Arrays.stream(values()).filter(rule -> rule.violated.test(car)).findFirst();
    }
}
