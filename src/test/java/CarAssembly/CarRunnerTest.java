package CarAssembly;

import CarEnums.BrakeSystem;
import CarEnums.CarType;
import CarEnums.Engine;
import CarEnums.SteeringSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class CarRunnerTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final CarRunner carRunner = new CarRunner(new ConsoleView());

    @BeforeEach
    void redirectSystemOut() {
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("규칙 위반 조합을 run()하면 동작하지 않는다는 메시지를 출력한다")
    void run_invalidCombination_printsCannotOperate() {
        Car car = new Car(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);

        carRunner.run(car);

        assertThat(out.toString()).contains("자동차가 동작되지 않습니다");
    }

    @Test
    @DisplayName("고장난 엔진으로 run()하면 움직이지 않는다는 메시지를 출력한다")
    void run_brokenEngine_printsCannotMove() {
        Car car = new Car(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        carRunner.run(car);

        assertThat(out.toString()).contains("엔진이 고장나있습니다.");
        assertThat(out.toString()).contains("자동차가 움직이지 않습니다.");
    }

    @Test
    @DisplayName("정상 조합을 run()하면 차량 설명과 동작 메시지를 출력한다")
    void run_validCombination_printsDescriptionAndOperates() {
        Car car = new Car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        carRunner.run(car);

        assertThat(out.toString()).contains("Car Type : Sedan");
        assertThat(out.toString()).contains("자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("규칙 위반 조합을 test()하면 FAIL과 위반 사유를 출력한다")
    void test_invalidCombination_printsFail() {
        Car car = new Car(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        carRunner.test(car);

        assertThat(out.toString()).contains("테스트 결과 : FAIL");
        assertThat(out.toString()).contains("SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    @DisplayName("정상 조합을 test()하면 PASS를 출력한다")
    void test_validCombination_printsPass() {
        Car car = new Car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        carRunner.test(car);

        assertThat(out.toString()).contains("테스트 결과 : PASS");
    }
}