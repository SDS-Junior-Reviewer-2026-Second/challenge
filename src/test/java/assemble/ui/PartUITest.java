package assemble.ui;

import car.Car;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parts.BrakeSystem;
import parts.CarType;
import parts.Engine;
import parts.SteeringSystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PartUITest {
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
    @DisplayName("차량 종류 UI는 메뉴를 출력하고 선택을 적용한다")
    void handlesCarType() {
        Car car = new Car();
        CarTypeUI ui = new CarTypeUI();

        ui.showMenu();
        StepAction action = ui.execute(car, 2);

        assertThat(ui.isValidInput(1)).isTrue();
        assertThat(ui.isValidInput(0)).isFalse();
        assertThat(ui.validationError()).isEqualTo("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
        assertThat(car.getCarType()).isEqualTo(CarType.SUV);
        assertThat(action).isEqualTo(StepAction.NEXT);
        assertThat(console()).contains("어떤 차량 타입을 선택할까요?", "1. Sedan", "차량 타입으로 SUV을 선택하셨습니다.");
    }

    @Test
    @DisplayName("엔진 UI는 뒤로가기와 모든 엔진을 제공하고 선택을 적용한다")
    void handlesEngine() {
        Car car = new Car();
        EngineUI ui = new EngineUI();

        ui.showMenu();
        ui.execute(car, 4);

        assertThat(ui.isValidInput(0)).isTrue();
        assertThat(ui.isValidInput(5)).isFalse();
        assertThat(ui.validationError()).isEqualTo("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
        assertThat(car.getEngine()).isEqualTo(Engine.BROKEN);
        assertThat(console()).contains("0. 뒤로가기", "4. 고장난 엔진", "고장난 엔진 엔진을 선택하셨습니다.");
    }

    @Test
    @DisplayName("제동장치 UI는 메뉴를 출력하고 선택을 적용한다")
    void handlesBrakeSystem() {
        Car car = new Car();
        BrakeSystemUI ui = new BrakeSystemUI();

        ui.showMenu();
        ui.execute(car, 3);

        assertThat(ui.isValidInput(0)).isTrue();
        assertThat(ui.isValidInput(4)).isFalse();
        assertThat(ui.validationError()).isEqualTo("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
        assertThat(car.getBrakeSystem()).isEqualTo(BrakeSystem.BOSCH);
        assertThat(console()).contains("3. BOSCH", "Bosch 제동장치를 선택하셨습니다.");
    }

    @Test
    @DisplayName("조향장치 UI는 메뉴를 출력하고 선택을 적용한다")
    void handlesSteeringSystem() {
        Car car = new Car();
        SteeringSystemUI ui = new SteeringSystemUI();

        ui.showMenu();
        ui.execute(car, 2);

        assertThat(ui.isValidInput(0)).isTrue();
        assertThat(ui.isValidInput(3)).isFalse();
        assertThat(ui.validationError()).isEqualTo("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
        assertThat(car.getSteeringSystem()).isEqualTo(SteeringSystem.MOBIS);
        assertThat(console()).contains("2. MOBIS", "Mobis 조향장치를 선택하셨습니다.");
    }

    private String console() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
