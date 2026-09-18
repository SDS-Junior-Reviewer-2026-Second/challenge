package mission2.car.rule;

import mission2.car.Car;

import java.util.Optional;

public interface Rule {

    Optional<String> violatedBy(Car car);
}
