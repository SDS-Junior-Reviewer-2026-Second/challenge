import car.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import validation.CarValidator;
import validation.ValidationResult;

import static org.junit.jupiter.api.Assertions.*;

class CarValidatorTest {

    private CarValidator validator;
    private Car car;

    @BeforeEach
    void setUp() {
        validator = new CarValidator();
        car = new Car();
    }

    private void assemble(
            CarType type,
            Engine engine,
            BrakeSystem brake,
            SteeringSystem steering
    ) {
        car.selectCarType(type);
        car.selectEngine(engine);
        car.selectBrakeSystem(brake);
        car.selectSteeringSystem(steering);
    }

    @Test
    @DisplayName("정상적인 부품 조합은 PASS")
    void validCombination() {

        assemble(
                CarType.SEDAN,
                Engine.GM,
                BrakeSystem.MANDO,
                SteeringSystem.BOSCH
        );

        ValidationResult result = validator.validate(car);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("Sedan에는 Continental 제동장치를 사용할 수 없다")
    void sedanCannotUseContinentalBrake() {

        assemble(
                CarType.SEDAN,
                Engine.GM,
                BrakeSystem.CONTINENTAL,
                SteeringSystem.BOSCH
        );

        ValidationResult result = validator.validate(car);

        assertFalse(result.isValid());
        assertEquals(
                "Sedan에는 Continental 제동장치 사용 불가",
                result.getMessage()
        );
    }

    @Test
    @DisplayName("SUV에는 Toyota 엔진을 사용할 수 없다")
    void suvCannotUseToyotaEngine() {

        assemble(
                CarType.SUV,
                Engine.TOYOTA,
                BrakeSystem.MANDO,
                SteeringSystem.BOSCH
        );

        ValidationResult result = validator.validate(car);

        assertFalse(result.isValid());
        assertEquals(
                "SUV에는 TOYOTA 엔진 사용 불가",
                result.getMessage()
        );
    }

    @Test
    @DisplayName("Truck에는 WIA 엔진을 사용할 수 없다")
    void truckCannotUseWiaEngine() {

        assemble(
                CarType.TRUCK,
                Engine.WIA,
                BrakeSystem.BOSCH,
                SteeringSystem.BOSCH
        );

        ValidationResult result = validator.validate(car);

        assertFalse(result.isValid());
        assertEquals(
                "Truck에는 WIA 엔진 사용 불가",
                result.getMessage()
        );
    }

    @Test
    @DisplayName("Truck에는 Mando 제동장치를 사용할 수 없다")
    void truckCannotUseMandoBrake() {

        assemble(
                CarType.TRUCK,
                Engine.GM,
                BrakeSystem.MANDO,
                SteeringSystem.BOSCH
        );

        ValidationResult result = validator.validate(car);

        assertFalse(result.isValid());
        assertEquals(
                "Truck에는 Mando 제동장치 사용 불가",
                result.getMessage()
        );
    }

    @Test
    @DisplayName("Bosch 제동장치는 Bosch 조향장치와만 조합 가능")
    void boschBrakeRequiresBoschSteering() {

        assemble(
                CarType.SUV,
                Engine.GM,
                BrakeSystem.BOSCH,
                SteeringSystem.MOBIS
        );

        ValidationResult result = validator.validate(car);

        assertFalse(result.isValid());
        assertEquals(
                "Bosch 제동장치에는 Bosch 조향장치 이외 사용 불가",
                result.getMessage()
        );
    }
}