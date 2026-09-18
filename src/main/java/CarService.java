import java.util.Objects;
import java.util.Optional;

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

    RunStatus run(CarConfiguration car) {
        if (compatibilityPolicy.findViolation(car).isPresent()) {
            return RunStatus.INCOMPATIBLE;
        }
        if (car.engine() == Engine.BROKEN) {
            return RunStatus.BROKEN_ENGINE;
        }
        return RunStatus.RUNNING;
    }

    Optional<String> test(CarConfiguration car) {
        return compatibilityPolicy.findViolation(car);
    }
}
