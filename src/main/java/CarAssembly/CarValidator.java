package CarAssembly;

import CarEnums.CarCompatibilityRule;

import java.util.Optional;

public class CarValidator {

    public Optional<String> findFirstViolation(Car car) {
        for (CarCompatibilityRule rule : CarCompatibilityRule.values()) {
            if (rule.isViolatedBy(car)) {
                return Optional.of(rule.violationMessage());
            }
        }
        return Optional.empty();
    }
}