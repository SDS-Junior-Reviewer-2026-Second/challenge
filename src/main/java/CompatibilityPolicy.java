import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

final class CompatibilityPolicy {
    private final List<Rule> rules = List.of(
            rule(car -> car.carType() == CarType.SEDAN
                            && car.brakeSystem() == BrakeSystem.CONTINENTAL,
                    "Sedan에는 Continental제동장치 사용 불가"),
            rule(car -> car.carType() == CarType.SUV
                            && car.engine() == Engine.TOYOTA,
                    "SUV에는 TOYOTA엔진 사용 불가"),
            rule(car -> car.carType() == CarType.TRUCK
                            && car.engine() == Engine.WIA,
                    "Truck에는 WIA엔진 사용 불가"),
            rule(car -> car.carType() == CarType.TRUCK
                            && car.brakeSystem() == BrakeSystem.MANDO,
                    "Truck에는 Mando제동장치 사용 불가"),
            rule(car -> car.brakeSystem() == BrakeSystem.BOSCH
                            && car.steeringSystem() != SteeringSystem.BOSCH,
                    "Bosch제동장치에는 Bosch조향장치 이외 사용 불가")
    );

    Optional<String> findViolation(CarConfiguration car) {
        for (Rule rule : rules) {
            if (rule.violatedBy(car)) {
                return Optional.of(rule.message());
            }
        }
        return Optional.empty();
    }

    private static Rule rule(Predicate<CarConfiguration> condition, String message) {
        return new Rule(condition, message);
    }

    private record Rule(Predicate<CarConfiguration> condition, String message) {
        boolean violatedBy(CarConfiguration car) {
            return condition.test(car);
        }
    }
}
