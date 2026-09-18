import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CarTest {

    private static Car carOf(CarType carType, Engine engine, BrakeSystem brake, SteeringSystem steering) {
        Car car = new Car();
        car.setCarType(carType);
        car.setEngine(engine);
        car.setBrakeSystem(brake);
        car.setSteeringSystem(steering);
        return car;
    }

    // ---- run(): printed outcome per part combination ----

    private record RunCase(String description, Car car, String[] mustContain, String[] mustNotContain) {}

    private static Stream<RunCase> runCases() {
        return Stream.of(
                new RunCase("invalid part combination -> car does not run",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH),
                        new String[]{"자동차가 동작되지 않습니다"},
                        new String[]{"자동차가 동작됩니다", "엔진이 고장나있습니다."}),
                new RunCase("valid combination + broken engine -> engine failure",
                        carOf(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.BOSCH),
                        new String[]{"엔진이 고장나있습니다.", "자동차가 움직이지 않습니다."},
                        new String[]{"자동차가 동작됩니다."}),
                new RunCase("fully valid combination -> car runs with part details",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH),
                        new String[]{"Car Type : Sedan", "Engine   : GM", "Brake    : Mando",
                                "Steering : Bosch", "자동차가 동작됩니다."},
                        new String[]{}),
                new RunCase("valid bosch brake+steering combination -> car runs",
                        carOf(CarType.SUV, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH),
                        new String[]{"Car Type : SUV", "Brake    : Bosch", "Steering : Bosch",
                                "자동차가 동작됩니다."},
                        new String[]{}),
                new RunCase("valid continental brake combination -> car runs",
                        carOf(CarType.SUV, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.MOBIS),
                        new String[]{"Car Type : SUV", "Brake    : Continental", "Steering : Mobis",
                                "자동차가 동작됩니다."},
                        new String[]{})
        );
    }

    @TestFactory
    Stream<DynamicTest> run_partCombination_printsExpectedOutcome() {
        return runCases().map(tc -> dynamicTest(tc.description(), () -> {
            String output = ConsoleCapture.captureStdOut(tc.car()::run);
            if (tc.mustContain().length > 0) {
                assertThat(output).contains(tc.mustContain());
            }
            if (tc.mustNotContain().length > 0) {
                assertThat(output).doesNotContain(tc.mustNotContain());
            }
        }));
    }

    // ---- test(): printed PASS/FAIL result per part combination ----

    private static final String FAIL_HEADER = "자동차 부품 조합 테스트 결과 : FAIL";
    private static final String PASS_HEADER = "자동차 부품 조합 테스트 결과 : PASS";

    private record TestCase(String description, Car car, String expectedHeader, String expectedDetail) {}

    private static Stream<TestCase> testCases() {
        return Stream.of(
                new TestCase("sedan+continentalBrake -> FAIL",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH),
                        FAIL_HEADER, "Sedan에는 Continental제동장치 사용 불가"),
                new TestCase("suv+toyotaEngine -> FAIL",
                        carOf(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH),
                        FAIL_HEADER, "SUV에는 TOYOTA엔진 사용 불가"),
                new TestCase("truck+wiaEngine -> FAIL",
                        carOf(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH),
                        FAIL_HEADER, "Truck에는 WIA엔진 사용 불가"),
                new TestCase("truck+mandoBrake -> FAIL",
                        carOf(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH),
                        FAIL_HEADER, "Truck에는 Mando제동장치 사용 불가"),
                new TestCase("boschBrake+nonBoschSteering -> FAIL",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS),
                        FAIL_HEADER, "Bosch제동장치에는 Bosch조향장치 이외 사용 불가"),
                new TestCase("baseline valid combination -> PASS",
                        carOf(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH),
                        PASS_HEADER, null)
        );
    }

    @TestFactory
    Stream<DynamicTest> test_partCombination_printsExpectedResult() {
        return testCases().map(tc -> dynamicTest(tc.description(), () -> {
            String output = ConsoleCapture.captureStdOut(tc.car()::test);
            String otherHeader = tc.expectedHeader().equals(PASS_HEADER) ? FAIL_HEADER : PASS_HEADER;
            assertThat(output).contains(tc.expectedHeader()).doesNotContain(otherHeader);
            if (tc.expectedDetail() != null) {
                assertThat(output).contains(tc.expectedDetail());
            }
        }));
    }

    // A broken engine only affects run(); test() only checks part-combination compatibility.
    @Test
    void test_brokenEngineWithOtherwiseValidCombination_stillPasses() throws Exception {
        Car car = carOf(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        String output = ConsoleCapture.captureStdOut(car::test);
        assertThat(output).contains(PASS_HEADER);
    }
}
