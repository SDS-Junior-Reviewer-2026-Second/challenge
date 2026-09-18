package org.example.assemble;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class CompatibilityPolicy {
    private final List<CompatibilityRule> rules;

    public CompatibilityPolicy() {
        this(defaultRules());
    }

    public CompatibilityPolicy(List<CompatibilityRule> rules) {
        this.rules = List.copyOf(Objects.requireNonNull(rules, "rules"));
    }

    public CompatibilityResult evaluate(VehicleConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration");

        return rules.stream()
                .map(rule -> rule.findViolation(configuration))
                .flatMap(Optional::stream)
                .findFirst()
                .map(CompatibilityResult::fail)
                .orElseGet(CompatibilityResult::pass);
    }

    private static List<CompatibilityRule> defaultRules() {
        return List.of(
                rule(
                        configuration -> configuration.carType() == CarType.SEDAN
                                && configuration.brakeSystem() == BrakeSystem.CONTINENTAL,
                        "Sedan에는 Continental제동장치 사용 불가"
                ),
                rule(
                        configuration -> configuration.carType() == CarType.SUV
                                && configuration.engine() == Engine.TOYOTA,
                        "SUV에는 TOYOTA엔진 사용 불가"
                ),
                rule(
                        configuration -> configuration.carType() == CarType.TRUCK
                                && configuration.engine() == Engine.WIA,
                        "Truck에는 WIA엔진 사용 불가"
                ),
                rule(
                        configuration -> configuration.carType() == CarType.TRUCK
                                && configuration.brakeSystem() == BrakeSystem.MANDO,
                        "Truck에는 Mando제동장치 사용 불가"
                ),
                rule(
                        configuration -> configuration.brakeSystem() == BrakeSystem.BOSCH
                                && configuration.steeringSystem() != SteeringSystem.BOSCH,
                        "Bosch제동장치에는 Bosch조향장치 이외 사용 불가"
                )
        );
    }

    private static CompatibilityRule rule(
            Predicate<VehicleConfiguration> predicate,
            String failureReason
    ) {
        return configuration -> predicate.test(configuration)
                ? Optional.of(failureReason)
                : Optional.empty();
    }
}
