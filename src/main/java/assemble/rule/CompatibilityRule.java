package assemble.rule;

import assemble.model.CarSpec;

import java.util.function.Predicate;

/** 부품 조합 제약 하나. violatedWhen 이 참이면 failMessage 로 실패한다. */
public record CompatibilityRule(Predicate<CarSpec> violatedWhen, String failMessage) {

    public boolean isViolatedBy(CarSpec spec) {
        return violatedWhen.test(spec);
    }
}
