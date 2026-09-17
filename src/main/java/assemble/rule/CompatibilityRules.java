package assemble.rule;

import assemble.model.BrakeSystem;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.SteeringSystem;

import java.util.List;

/** 부품 조합 제약의 단일 정의. 평가는 CarInspector 가 한다. */
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
}
