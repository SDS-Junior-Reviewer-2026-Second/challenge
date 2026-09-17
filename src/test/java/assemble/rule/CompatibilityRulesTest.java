package assemble.rule;

import assemble.model.BrakeSystem;
import assemble.model.CarSpec;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.SteeringSystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompatibilityRulesTest {

    private static final CarSpec VALID_SEDAN =
            new CarSpec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

    @Test
    void validCombinationHasNoViolations() {
        assertThat(CompatibilityRules.violations(VALID_SEDAN)).isEmpty();
        assertThat(CompatibilityRules.isCompatible(VALID_SEDAN)).isTrue();
    }

    @Test
    void sedanWithContinentalBrakeIsRejected() {
        CarSpec spec = VALID_SEDAN.withBrake(BrakeSystem.CONTINENTAL);

        assertThat(CompatibilityRules.violations(spec))
                .containsExactly("Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    void suvWithToyotaEngineIsRejected() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.SUV).withEngine(Engine.TOYOTA);

        assertThat(CompatibilityRules.violations(spec))
                .containsExactly("SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    void suvWithOtherEngineIsAccepted() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.SUV).withEngine(Engine.GM);

        assertThat(CompatibilityRules.violations(spec)).isEmpty();
    }

    @Test
    void truckWithWiaEngineIsRejected() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.TRUCK).withEngine(Engine.WIA)
                .withBrake(BrakeSystem.CONTINENTAL);

        assertThat(CompatibilityRules.violations(spec))
                .containsExactly("Truck에는 WIA엔진 사용 불가");
    }

    @Test
    void truckWithMandoBrakeIsRejected() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.TRUCK);

        assertThat(CompatibilityRules.violations(spec))
                .containsExactly("Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    void boschBrakeRequiresBoschSteering() {
        CarSpec ok = VALID_SEDAN.withBrake(BrakeSystem.BOSCH).withSteering(SteeringSystem.BOSCH);
        CarSpec bad = ok.withSteering(SteeringSystem.MOBIS);

        assertThat(CompatibilityRules.violations(ok)).isEmpty();
        assertThat(CompatibilityRules.violations(bad))
                .containsExactly("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
    }

    @Test
    void multipleViolationsAreReportedInDefinitionOrder() {
        CarSpec spec = new CarSpec(CarType.TRUCK, Engine.WIA, BrakeSystem.MANDO, SteeringSystem.MOBIS);

        assertThat(CompatibilityRules.violations(spec)).containsExactly(
                "Truck에는 WIA엔진 사용 불가",
                "Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    void brokenEngineIsNotACompatibilityViolation() {
        CarSpec spec = VALID_SEDAN.withEngine(Engine.BROKEN);

        assertThat(CompatibilityRules.violations(spec)).isEmpty();
    }
}
