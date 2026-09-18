package CarAssembly;

import CarEnums.BrakeSystem;
import CarEnums.CarType;
import CarEnums.Engine;
import CarEnums.SteeringSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CarValidatorTest {

    private final CarValidator validator = new CarValidator();

    private Car car(CarType type, Engine engine, BrakeSystem brake, SteeringSystem steering) {
        return new Car(type, engine, brake, steering);
    }

    @Test
    @DisplayName("모든 규칙을 위반하지 않는 조합이면 위반 사항이 없다")
    void findFirstViolation_empty_whenValidCombination() {
        Car car = car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertThat(validator.findFirstViolation(car)).isEmpty();
    }

    @Test
    @DisplayName("Sedan에 Continental 제동장치는 사용할 수 없다")
    void sedan_cannotUseContinentalBrake() {
        Car car = car(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);

        Optional<String> violation = validator.findFirstViolation(car);

        assertThat(violation).contains("Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    @DisplayName("SUV에 TOYOTA 엔진은 사용할 수 없다")
    void suv_cannotUseToyotaEngine() {
        Car car = car(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        Optional<String> violation = validator.findFirstViolation(car);

        assertThat(violation).contains("SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck에 WIA 엔진은 사용할 수 없다")
    void truck_cannotUseWiaEngine() {
        Car car = car(CarType.TRUCK, Engine.WIA, BrakeSystem.BOSCH, SteeringSystem.BOSCH);

        Optional<String> violation = validator.findFirstViolation(car);

        assertThat(violation).contains("Truck에는 WIA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck에 Mando 제동장치는 사용할 수 없다")
    void truck_cannotUseMandoBrake() {
        Car car = car(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        Optional<String> violation = validator.findFirstViolation(car);

        assertThat(violation).contains("Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    @DisplayName("Bosch 제동장치는 Bosch 조향장치와만 함께 쓸 수 있다")
    void boschBrake_requiresBoschSteering() {
        Car car = car(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS);

        Optional<String> violation = validator.findFirstViolation(car);

        assertThat(violation).contains("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
    }

    @Test
    @DisplayName("여러 규칙을 동시에 위반해도 CarCompatibilityRule에 먼저 정의된 규칙 하나만 보고한다")
    void findFirstViolation_returnsOnlyFirstMatchingRule_whenMultipleViolated() {
        // Truck + WIA엔진 + Mando제동장치 조합은 두 규칙(WIA엔진 금지, Mando제동장치 금지)을 동시에 위반한다.
        // enum 선언 순서상 TRUCK_CANNOT_USE_WIA_ENGINE이 먼저이므로 그 메시지만 반환되어야 한다.
        Car car = car(CarType.TRUCK, Engine.WIA, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        Optional<String> violation = validator.findFirstViolation(car);

        assertThat(violation).contains("Truck에는 WIA엔진 사용 불가");
    }
}