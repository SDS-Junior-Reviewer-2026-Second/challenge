package assemble.rule;

import assemble.model.BrakeSystem;
import assemble.model.CarSpec;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.SteeringSystem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarInspectorTest {

    private static final CarInspector INSPECTOR = new CarInspector(CompatibilityRules.ALL);

    private static final CarSpec VALID_SEDAN =
            new CarSpec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

    @Test
    void validCombinationHasNoViolations() {
        assertThat(INSPECTOR.violations(VALID_SEDAN)).isEmpty();
        assertThat(INSPECTOR.isCompatible(VALID_SEDAN)).isTrue();
    }

    @Test
    void sedanWithContinentalBrakeIsRejected() {
        CarSpec spec = VALID_SEDAN.withBrake(BrakeSystem.CONTINENTAL);

        assertThat(INSPECTOR.violations(spec))
                .containsExactly("Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    void suvWithToyotaEngineIsRejected() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.SUV).withEngine(Engine.TOYOTA);

        assertThat(INSPECTOR.violations(spec))
                .containsExactly("SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    void suvWithOtherEngineIsAccepted() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.SUV).withEngine(Engine.GM);

        assertThat(INSPECTOR.violations(spec)).isEmpty();
    }

    @Test
    void truckWithWiaEngineIsRejected() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.TRUCK).withEngine(Engine.WIA)
                .withBrake(BrakeSystem.CONTINENTAL);

        assertThat(INSPECTOR.violations(spec))
                .containsExactly("Truck에는 WIA엔진 사용 불가");
    }

    @Test
    void truckWithMandoBrakeIsRejected() {
        CarSpec spec = VALID_SEDAN.withCarType(CarType.TRUCK);

        assertThat(INSPECTOR.violations(spec))
                .containsExactly("Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    void boschBrakeRequiresBoschSteering() {
        CarSpec ok = VALID_SEDAN.withBrake(BrakeSystem.BOSCH).withSteering(SteeringSystem.BOSCH);
        CarSpec bad = ok.withSteering(SteeringSystem.MOBIS);

        assertThat(INSPECTOR.violations(ok)).isEmpty();
        assertThat(INSPECTOR.violations(bad))
                .containsExactly("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
    }

    @Test
    void multipleViolationsAreReportedInDefinitionOrder() {
        CarSpec spec = new CarSpec(CarType.TRUCK, Engine.WIA, BrakeSystem.MANDO, SteeringSystem.MOBIS);

        assertThat(INSPECTOR.violations(spec)).containsExactly(
                "Truck에는 WIA엔진 사용 불가",
                "Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    void brokenEngineIsNotACompatibilityViolation() {
        CarSpec spec = VALID_SEDAN.withEngine(Engine.BROKEN);

        assertThat(INSPECTOR.violations(spec)).isEmpty();
    }

    @Test
    void runReportsIncompatibleBeforeBrokenEngine() {
        CarSpec incompatibleAndBroken = VALID_SEDAN.withEngine(Engine.BROKEN).withBrake(BrakeSystem.CONTINENTAL);

        assertThat(INSPECTOR.run(incompatibleAndBroken)).isEqualTo(RunResult.INCOMPATIBLE);
    }

    @Test
    void runReportsBrokenEngineForCompatibleSpec() {
        assertThat(INSPECTOR.run(VALID_SEDAN.withEngine(Engine.BROKEN))).isEqualTo(RunResult.ENGINE_BROKEN);
    }

    @Test
    void runReportsRunnableForValidSpec() {
        assertThat(INSPECTOR.run(VALID_SEDAN)).isEqualTo(RunResult.RUNNABLE);
    }

    @Test
    void inspectorWithNoRulesAcceptsEverything() {
        CarInspector lenient = new CarInspector(List.of());

        assertThat(lenient.violations(VALID_SEDAN.withBrake(BrakeSystem.CONTINENTAL))).isEmpty();
    }
}
