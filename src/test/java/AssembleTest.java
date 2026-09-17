import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AssembleTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("각 단계는 정의된 입력 범위만 허용한다")
    void validatesEveryMenuRange() {
        assertAcceptsAll(AssemblyStep.CAR_TYPE, CarType.values());
        assertThat(AssemblyStep.CAR_TYPE.isValidInput(0)).isFalse();
        assertThat(AssemblyStep.CAR_TYPE.isValidInput(Integer.MAX_VALUE)).isFalse();

        assertAcceptsAll(AssemblyStep.ENGINE, Engine.values());
        assertThat(AssemblyStep.ENGINE.isValidInput(0)).isTrue();
        assertThat(AssemblyStep.ENGINE.isValidInput(-1)).isFalse();
        assertThat(AssemblyStep.ENGINE.isValidInput(Integer.MAX_VALUE)).isFalse();

        assertAcceptsAll(AssemblyStep.BRAKE_SYSTEM, BrakeSystem.values());
        assertThat(AssemblyStep.BRAKE_SYSTEM.isValidInput(0)).isTrue();
        assertThat(AssemblyStep.BRAKE_SYSTEM.isValidInput(Integer.MAX_VALUE)).isFalse();

        assertAcceptsAll(AssemblyStep.STEERING_SYSTEM, SteeringSystem.values());
        assertThat(AssemblyStep.STEERING_SYSTEM.isValidInput(0)).isTrue();
        assertThat(AssemblyStep.STEERING_SYSTEM.isValidInput(Integer.MAX_VALUE)).isFalse();

        assertThat(AssemblyStep.RUN_TEST.isValidInput(0)).isTrue();
        assertThat(AssemblyStep.RUN_TEST.isValidInput(2)).isTrue();
        assertThat(AssemblyStep.RUN_TEST.isValidInput(3)).isFalse();

        assertThat(AssemblyStep.CAR_TYPE.validationError())
                .isEqualTo("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
        assertThat(AssemblyStep.ENGINE.validationError())
                .isEqualTo("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
        assertThat(AssemblyStep.BRAKE_SYSTEM.validationError())
                .isEqualTo("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
        assertThat(AssemblyStep.STEERING_SYSTEM.validationError())
                .isEqualTo("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
    }

    private void assertAcceptsAll(AssemblyStep step, SelectablePart[] parts) {
        for (SelectablePart part : parts) {
            assertThat(step.isValidInput(part.getCode()))
                    .as("%s 코드 %d", part.getDisplayName(), part.getCode())
                    .isTrue();
        }
    }

    @Test
    @DisplayName("조립 단계는 정의된 순서대로 앞뒤로 이동한다")
    void movesBetweenAssemblySteps() {
        AssemblyFlow flow = new AssemblyFlow();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.CAR_TYPE);

        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.ENGINE);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.BRAKE_SYSTEM);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.STEERING_SYSTEM);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.RUN_TEST);

        flow.moveBack();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.CAR_TYPE);
        flow.moveNext();
        flow.moveNext();
        flow.moveBack();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.ENGINE);
    }

    @Test
    @DisplayName("부품 선택은 조립 상태에 저장된다")
    void selectionsAreStored() {
        Car car = new Car();
        car.selectCarType(2);
        car.selectEngine(3);
        car.selectBrakeSystem(1);
        car.selectSteeringSystem(2);

        assertThat(car.getCarType()).isEqualTo(CarType.SUV);
        assertThat(car.getEngine()).isEqualTo(Engine.WIA);
        assertThat(car.getBrakeSystem()).isEqualTo(BrakeSystem.MANDO);
        assertThat(car.getSteeringSystem()).isEqualTo(SteeringSystem.MOBIS);
    }

    @Test
    @DisplayName("허용되는 부품 조합은 유효하다")
    void acceptsCompatibleCombination() {
        Car car = createCar(1, 1, 1, 2);

        assertThat(car.isValid()).isTrue();
    }

    @Test
    @DisplayName("Sedan과 Continental 브레이크 조합은 거부한다")
    void rejectsSedanWithContinentalBrake() {
        assertInvalidCombination(1, 1, 2, 1, "Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    @DisplayName("SUV와 Toyota 엔진 조합은 거부한다")
    void rejectsSuvWithToyotaEngine() {
        assertInvalidCombination(2, 2, 1, 1, "SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck과 WIA 엔진 조합은 거부한다")
    void rejectsTruckWithWiaEngine() {
        assertInvalidCombination(3, 3, 3, 1, "Truck에는 WIA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck과 Mando 브레이크 조합은 거부한다")
    void rejectsTruckWithMandoBrake() {
        assertInvalidCombination(3, 1, 1, 1, "Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    @DisplayName("Bosch 브레이크에는 Bosch 조향장치만 허용한다")
    void rejectsBoschBrakeWithNonBoschSteering() {
        assertInvalidCombination(1, 1, 3, 2, "Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
    }

    @Test
    @DisplayName("정상 차량 실행 시 선택 사양과 동작 메시지를 출력한다")
    void runsValidCar() {
        Car car = createCar(2, 3, 2, 2);

        car.runProducedCar();

        assertThat(console())
                .contains("Car Type : SUV")
                .contains("Engine   : WIA")
                .contains("Brake    : Continental")
                .contains("Steering : Mobis")
                .contains("자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("고장난 엔진 차량은 움직이지 않는다")
    void doesNotRunWithBrokenEngine() {
        Car car = createCar(1, 4, 1, 1);

        car.runProducedCar();

        assertThat(console())
                .contains("엔진이 고장나있습니다.")
                .contains("자동차가 움직이지 않습니다.")
                .doesNotContain("자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("유효하지 않은 조합의 차량은 실행되지 않는다")
    void doesNotRunInvalidCombination() {
        Car car = createCar(1, 1, 2, 1);

        car.runProducedCar();

        assertThat(console()).contains("자동차가 동작되지 않습니다");
    }

    @Test
    @DisplayName("유효한 조합 테스트는 PASS를 출력한다")
    void reportsPassForValidCombination() {
        Car car = createCar(1, 1, 1, 2);

        car.testProducedCar();

        assertThat(console()).contains("자동차 부품 조합 테스트 결과 : PASS");
    }

    private void assertInvalidCombination(int car, int engine, int brake, int steering, String reason)
    {
        Car producedCar = createCar(car, engine, brake, steering);

        assertThat(producedCar.isValid()).isFalse();
        producedCar.testProducedCar();
        assertThat(console())
                .contains("자동차 부품 조합 테스트 결과 : FAIL")
                .contains(reason);
    }

    private Car createCar(int carType, int engine, int brake, int steering) {
        Car car = new Car();
        car.selectCarType(carType);
        car.selectEngine(engine);
        car.selectBrakeSystem(brake);
        car.selectSteeringSystem(steering);
        return car;
    }

    private String console() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
