package domain.rule;

import domain.model.BrakeSystem;
import domain.model.CarConfig;
import domain.model.CarType;
import domain.model.Engine;
import domain.model.SteeringSystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompatibilityCheckerTest {

    private static CarConfig configOf(CarType carType, Engine engine, BrakeSystem brake, SteeringSystem steering) {
        CarConfig config = new CarConfig();
        config.setCarType(carType);
        config.setEngine(engine);
        config.setBrake(brake);
        config.setSteering(steering);
        return config;
    }

    @Test
    void allCompatibleParts_haveNoViolations() {
        CarConfig config = configOf(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertThat(CompatibilityChecker.findViolations(config)).isEmpty();
        assertThat(CompatibilityChecker.isValid(config)).isTrue();
    }

    @Test
    void sedanWithContinentalBrake_isRejected() {
        CarConfig config = configOf(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);

        assertThat(CompatibilityChecker.findViolations(config))
                .containsExactly("Sedan에는 Continental제동장치 사용 불가");
        assertThat(CompatibilityChecker.isValid(config)).isFalse();
    }

    @Test
    void suvWithToyotaEngine_isRejected() {
        CarConfig config = configOf(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertThat(CompatibilityChecker.findViolations(config))
                .containsExactly("SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    void truckWithWiaEngine_isRejected() {
        CarConfig config = configOf(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);

        assertThat(CompatibilityChecker.findViolations(config))
                .containsExactly("Truck에는 WIA엔진 사용 불가");
    }

    @Test
    void truckWithMandoBrake_isRejected() {
        CarConfig config = configOf(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertThat(CompatibilityChecker.findViolations(config))
                .containsExactly("Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    void boschBrakeWithNonBoschSteering_isRejected() {
        CarConfig config = configOf(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS);

        assertThat(CompatibilityChecker.findViolations(config))
                .containsExactly("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
    }

    @Test
    void boschBrakeWithBoschSteering_isAllowed() {
        CarConfig config = configOf(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH);

        assertThat(CompatibilityChecker.findViolations(config)).isEmpty();
    }

    @Test
    void multipleViolatedRules_areAllReported() {
        CarConfig config = configOf(CarType.TRUCK, Engine.WIA, BrakeSystem.MANDO, SteeringSystem.MOBIS);

        assertThat(CompatibilityChecker.findViolations(config))
                .containsExactlyInAnyOrder(
                        "Truck에는 WIA엔진 사용 불가",
                        "Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    void fromCode_rejectsUnknownCode() {
        assertThatThrownBy(() -> Engine.fromCode(99))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fromCode_mapsCodeToMatchingEnumConstant() {
        assertThat(CarType.fromCode(2)).isEqualTo(CarType.SUV);
        assertThat(Engine.fromCode(4)).isEqualTo(Engine.BROKEN);
    }
}
