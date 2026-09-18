package mission2.car.rule;

import mission2.car.Car;
import mission2.car.part.Brakes;
import mission2.car.part.CarTypes;
import mission2.car.part.Engines;
import mission2.car.part.Part;
import mission2.car.part.SteeringSystems;

import java.util.List;
import java.util.Optional;

public class CombinationRules {

    private static final List<Rule> ALL = List.of(
            forbid(CarTypes.SEDAN, Brakes.CONTINENTAL, "Sedan에는 Continental제동장치 사용 불가"),
            forbid(CarTypes.SUV, Engines.TOYOTA, "SUV에는 TOYOTA엔진 사용 불가"),
            forbid(CarTypes.TRUCK, Engines.WIA, "Truck에는 WIA엔진 사용 불가"),
            forbid(CarTypes.TRUCK, Brakes.MANDO, "Truck에는 Mando제동장치 사용 불가"),
            require(Brakes.BOSCH, SteeringSystems.BOSCH, "Bosch제동장치에는 Bosch조향장치 이외 사용 불가"));

    private CombinationRules() {
    }

    public static Optional<String> findViolation(Car car) {
        return ALL.stream()
                .map(rule -> rule.violatedBy(car))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private static Rule forbid(Part part, Part other, String reason) {
        return car -> reasonIf(car.has(part) && car.has(other), reason);
    }

    private static Rule require(Part part, Part other, String reason) {
        return car -> reasonIf(car.has(part) && !car.has(other), reason);
    }

    private static Optional<String> reasonIf(boolean violated, String reason) {
        return violated ? Optional.of(reason) : Optional.empty();
    }
}
