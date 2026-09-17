package domain.rule;

import domain.model.BrakeSystem;
import domain.model.CarConfig;
import domain.model.CarType;
import domain.model.Engine;
import domain.model.SteeringSystem;

import java.util.ArrayList;
import java.util.List;

public final class CompatibilityChecker {

    private CompatibilityChecker() {
    }

    @FunctionalInterface
    private interface CompatibilityPredicate {
        boolean isViolated(CarType carType, Engine engine, BrakeSystem brake, SteeringSystem steering);
    }

    private record CompatibilityRule(CompatibilityPredicate predicate, String violationMessage) {
    }

    private static final List<CompatibilityRule> RULES = List.of(
            new CompatibilityRule(
                    (carType, engine, brake, steering) -> carType == CarType.SEDAN && brake == BrakeSystem.CONTINENTAL,
                    "Sedan에는 Continental제동장치 사용 불가"),
            new CompatibilityRule(
                    (carType, engine, brake, steering) -> carType == CarType.SUV && engine == Engine.TOYOTA,
                    "SUV에는 TOYOTA엔진 사용 불가"),
            new CompatibilityRule(
                    (carType, engine, brake, steering) -> carType == CarType.TRUCK && engine == Engine.WIA,
                    "Truck에는 WIA엔진 사용 불가"),
            new CompatibilityRule(
                    (carType, engine, brake, steering) -> carType == CarType.TRUCK && brake == BrakeSystem.MANDO,
                    "Truck에는 Mando제동장치 사용 불가"),
            new CompatibilityRule(
                    (carType, engine, brake, steering) -> brake == BrakeSystem.BOSCH && steering != SteeringSystem.BOSCH,
                    "Bosch제동장치에는 Bosch조향장치 이외 사용 불가")
    );

    public static List<String> findViolations(CarConfig config) {
        List<String> violations = new ArrayList<>();
        for (CompatibilityRule rule : RULES) {
            if (rule.predicate().isViolated(config.getCarType(), config.getEngine(), config.getBrake(), config.getSteering())) {
                violations.add(rule.violationMessage());
            }
        }
        return violations;
    }

    public static boolean isValid(CarConfig config) {
        return findViolations(config).isEmpty();
    }
}
