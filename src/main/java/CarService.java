import java.util.Objects;
import java.util.Optional;

/** 완성 차량에 대한 RUN/Test 유스케이스를 담당한다. */
final class CarService {
    enum RunStatus {
        INCOMPATIBLE,
        BROKEN_ENGINE,
        RUNNING
    }

    private final CompatibilityPolicy compatibilityPolicy;

    CarService(CompatibilityPolicy compatibilityPolicy) {
        this.compatibilityPolicy = Objects.requireNonNull(compatibilityPolicy);
    }

    RunStatus run(Car car) {
        if (compatibilityPolicy.findViolation(car).isPresent()) {
            return RunStatus.INCOMPATIBLE;
        }
        if (car.engine() == Engine.BROKEN) {
            return RunStatus.BROKEN_ENGINE;
        }
        return RunStatus.RUNNING;
    }

    Optional<String> test(Car car) {
        return compatibilityPolicy.findViolation(car);
    }
}
