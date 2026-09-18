package CarEnums;

import CarAssembly.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarCompatibilityRuleTest {

    private Car car(CarType type, Engine engine, BrakeSystem brake, SteeringSystem steering) {
        return new Car(type, engine, brake, steering);
    }

    @Test
    @DisplayName("SEDAN_CANNOT_USE_CONTINENTAL_BRAKE: Sedan + Continental 조합에서만 위반된다")
    void sedanCannotUseContinentalBrake() {
        CarCompatibilityRule rule = CarCompatibilityRule.SEDAN_CANNOT_USE_CONTINENTAL_BRAKE;

        assertThat(rule.isViolatedBy(car(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH))).isTrue();
        assertThat(rule.isViolatedBy(car(CarType.SUV, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH))).isFalse();
        assertThat(rule.isViolatedBy(car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH))).isFalse();
    }

    @Test
    @DisplayName("SUV_CANNOT_USE_TOYOTA_ENGINE: SUV + TOYOTA 조합에서만 위반된다")
    void suvCannotUseToyotaEngine() {
        CarCompatibilityRule rule = CarCompatibilityRule.SUV_CANNOT_USE_TOYOTA_ENGINE;

        assertThat(rule.isViolatedBy(car(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH))).isTrue();
        assertThat(rule.isViolatedBy(car(CarType.SUV, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH))).isFalse();
    }

    @Test
    @DisplayName("TRUCK_CANNOT_USE_WIA_ENGINE: Truck + WIA 조합에서만 위반된다")
    void truckCannotUseWiaEngine() {
        CarCompatibilityRule rule = CarCompatibilityRule.TRUCK_CANNOT_USE_WIA_ENGINE;

        assertThat(rule.isViolatedBy(car(CarType.TRUCK, Engine.WIA, BrakeSystem.BOSCH, SteeringSystem.BOSCH))).isTrue();
        assertThat(rule.isViolatedBy(car(CarType.TRUCK, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH))).isFalse();
    }

    @Test
    @DisplayName("TRUCK_CANNOT_USE_MANDO_BRAKE: Truck + Mando 조합에서만 위반된다")
    void truckCannotUseMandoBrake() {
        CarCompatibilityRule rule = CarCompatibilityRule.TRUCK_CANNOT_USE_MANDO_BRAKE;

        assertThat(rule.isViolatedBy(car(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH))).isTrue();
        assertThat(rule.isViolatedBy(car(CarType.TRUCK, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH))).isFalse();
    }

    @Test
    @DisplayName("BOSCH_BRAKE_REQUIRES_BOSCH_STEERING: Bosch제동장치 + Bosch가 아닌 조향장치 조합에서 위반된다")
    void boschBrakeRequiresBoschSteering() {
        CarCompatibilityRule rule = CarCompatibilityRule.BOSCH_BRAKE_REQUIRES_BOSCH_STEERING;

        assertThat(rule.isViolatedBy(car(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS))).isTrue();
        assertThat(rule.isViolatedBy(car(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH))).isFalse();
        assertThat(rule.isViolatedBy(car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.MOBIS))).isFalse();
    }

    @Test
    @DisplayName("각 규칙은 고유한 위반 메시지를 갖는다")
    void everyRule_hasNonBlankViolationMessage() {
        for (CarCompatibilityRule rule : CarCompatibilityRule.values()) {
            assertThat(rule.violationMessage()).isNotBlank();
        }
    }
}