import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CompatibilityRuleTest {

    private static Car carOf(CarType carType, Engine engine, BrakeSystem brake, SteeringSystem steering) {
        Car car = new Car();
        car.setCarType(carType);
        car.setEngine(engine);
        car.setBrakeSystem(brake);
        car.setSteeringSystem(steering);
        return car;
    }

    private record CheckCase(String description, Car car, boolean expectedViolation) {}

    private static Stream<CheckCase> cases() {
        return Stream.of(
                new CheckCase("sedan+continentalBrake -> violated",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH), true),
                new CheckCase("suv+toyotaEngine -> violated",
                        carOf(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH), true),
                new CheckCase("truck+wiaEngine -> violated",
                        carOf(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH), true),
                new CheckCase("truck+mandoBrake -> violated",
                        carOf(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH), true),
                new CheckCase("boschBrake+nonBoschSteering -> violated",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS), true),
                new CheckCase("baseline valid combination -> no violation",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH), false),
                new CheckCase("boschBrake+boschSteering -> no violation",
                        carOf(CarType.SUV, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH), false)
        );
    }

    @TestFactory
    Stream<DynamicTest> firstViolation_partCombination_matchesExpectedCompatibility() {
        return cases().map(tc -> dynamicTest(tc.description(), () ->
                assertThat(CompatibilityRule.firstViolation(tc.car()).isPresent()).isEqualTo(tc.expectedViolation())));
    }

    @TestFactory
    Stream<DynamicTest> firstViolation_violatedCase_carriesExpectedMessage() {
        record MessageCase(String description, Car car, String expectedMessage) {}
        return Stream.of(
                new MessageCase("sedan+continentalBrake",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH),
                        "Sedan에는 Continental제동장치 사용 불가"),
                new MessageCase("suv+toyotaEngine",
                        carOf(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH),
                        "SUV에는 TOYOTA엔진 사용 불가"),
                new MessageCase("truck+wiaEngine",
                        carOf(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH),
                        "Truck에는 WIA엔진 사용 불가"),
                new MessageCase("truck+mandoBrake",
                        carOf(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH),
                        "Truck에는 Mando제동장치 사용 불가"),
                new MessageCase("boschBrake+nonBoschSteering",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS),
                        "Bosch제동장치에는 Bosch조향장치 이외 사용 불가")
        ).map(tc -> dynamicTest(tc.description(), () ->
                assertThat(CompatibilityRule.firstViolation(tc.car()))
                        .get().extracting(CompatibilityRule::getMessage).isEqualTo(tc.expectedMessage())));
    }
}
