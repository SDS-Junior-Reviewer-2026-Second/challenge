package assemble.ui;

import assemble.AssemblyStep;
import car.Car;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

class CarAndRunTestUITest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void redirectOutput() {
        originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("정상 차량의 사양과 실행 성공을 출력한다")
    void printsSuccessfulRun() {
        new CarUI().runProducedCar(car(2, 3, 2, 2));

        assertThat(console()).contains(
                "Car Type : SUV", "Engine   : WIA", "Brake    : Continental",
                "Steering : Mobis", "자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("잘못된 조합과 고장 엔진은 실행 실패 이유를 출력한다")
    void printsRunFailures() {
        CarUI ui = new CarUI();
        ui.runProducedCar(car(1, 1, 2, 1));
        ui.runProducedCar(car(1, 4, 1, 1));

        assertThat(console()).contains(
                "자동차가 동작되지 않습니다",
                "엔진이 고장나있습니다.",
                "자동차가 움직이지 않습니다.");
    }

    @Test
    @DisplayName("차량 조합 테스트의 성공과 실패를 출력한다")
    void printsCombinationTestResult() {
        CarUI ui = new CarUI();
        ui.testProducedCar(car(1, 1, 1, 1));
        ui.testProducedCar(car(3, 3, 3, 1));

        assertThat(console()).contains(
                "자동차 부품 조합 테스트 결과 : PASS",
                "자동차 부품 조합 테스트 결과 : FAIL",
                "Truck에는 WIA엔진 사용 불가");
    }

    @Test
    @DisplayName("완성 UI는 실행 메뉴와 입력 범위를 제공한다")
    void showsAndValidatesRunTestMenu() {
        RunTestUI ui = new RunTestUI();
        ui.showMenu();

        assertThat(ui.isValidInput(AssemblyStep.BACK)).isTrue();
        assertThat(ui.isValidInput(AssemblyStep.RUN)).isTrue();
        assertThat(ui.isValidInput(AssemblyStep.TEST)).isTrue();
        assertThat(ui.isValidInput(3)).isFalse();
        assertThat(ui.validationError()).isEqualTo("ERROR :: Run 또는 Test 중 하나를 선택 필요");
        assertThat(console()).contains("멋진 차량이 완성되었습니다.", "0. 처음 화면으로 돌아가기", "1. RUN", "2. Test");
    }

    @Test
    @DisplayName("RUN과 Test 실행 후 현재 단계에 머문다")
    void executesRunAndTestWithoutChangingStep() {
        RunTestUI ui = new RunTestUI();
        Car car = car(1, 1, 1, 1);

        assertThat(ui.execute(car, AssemblyStep.RUN)).isEqualTo(StepAction.STAY);
        assertThat(ui.execute(car, AssemblyStep.TEST)).isEqualTo(StepAction.STAY);

        assertThat(console()).contains("자동차가 동작됩니다.", "Test...", "자동차 부품 조합 테스트 결과 : PASS");
    }

    private Car car(int type, int engine, int brake, int steering) {
        Car car = new Car();
        car.selectCarType(type);
        car.selectEngine(engine);
        car.selectBrakeSystem(brake);
        car.selectSteeringSystem(steering);
        return car;
    }

    private String console() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
