package assemble.rule;

import assemble.model.BrakeSystem;
import assemble.model.CarSpec;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.SteeringSystem;

import java.util.List;

/** 부품 조합 제약의 단일 정의. RUN 과 Test 가 모두 이 목록을 사용한다. */
public final class CompatibilityRules {

    public static final List<CompatibilityRule> ALL = List.of(
            new CompatibilityRule(
                    s -> s.carType() == CarType.SEDAN && s.brake() == BrakeSystem.CONTINENTAL,
                    "Sedan에는 Continental제동장치 사용 불가"),
            new CompatibilityRule(
                    s -> s.carType() == CarType.SUV && s.engine() == Engine.TOYOTA,
                    "SUV에는 TOYOTA엔진 사용 불가"),
            new CompatibilityRule(
                    s -> s.carType() == CarType.TRUCK && s.engine() == Engine.WIA,
                    "Truck에는 WIA엔진 사용 불가"),
            new CompatibilityRule(
                    s -> s.carType() == CarType.TRUCK && s.brake() == BrakeSystem.MANDO,
                    "Truck에는 Mando제동장치 사용 불가"),
            new CompatibilityRule(
                    s -> s.brake() == BrakeSystem.BOSCH && s.steering() != SteeringSystem.BOSCH,
                    "Bosch제동장치에는 Bosch조향장치 이외 사용 불가")
    );

    private CompatibilityRules() {}

    /** 위반한 규칙의 실패 메시지를 정의 순서대로 반환한다. 비어 있으면 통과. */
    public static List<String> violations(CarSpec spec) {
        return ALL.stream()
                .filter(rule -> rule.isViolatedBy(spec))
                .map(CompatibilityRule::failMessage)
                .toList();
    }

    public static boolean isCompatible(CarSpec spec) {
        return violations(spec).isEmpty();
    }
}
