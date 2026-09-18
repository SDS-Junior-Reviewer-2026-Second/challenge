import car.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.CarService;
import validation.CarValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class CarServiceTest {

    private CarService carService;
    private Car car;

    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {

        carService =
                new CarService(
                        new CarValidator()
                );

        car = new Car();

        originalOut = System.out;

        output =
                new ByteArrayOutputStream();

        System.setOut(
                new PrintStream(output)
        );
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
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
    @DisplayName("정상 차량은 실행된다")
    void runValidCar() {

        assemble(
                CarType.SEDAN,
                Engine.GM,
                BrakeSystem.MANDO,
                SteeringSystem.BOSCH
        );

        carService.run(car);

        assertTrue(
                output.toString()
                        .contains(
                                "자동차가 동작됩니다."
                        )
        );
    }

    @Test
    @DisplayName("잘못된 조합은 실행되지 않는다")
    void runInvalidCar() {

        assemble(
                CarType.SEDAN,
                Engine.GM,
                BrakeSystem.CONTINENTAL,
                SteeringSystem.BOSCH
        );

        carService.run(car);

        assertTrue(
                output.toString()
                        .contains(
                                "자동차가 동작되지 않습니다"
                        )
        );
    }

    @Test
    @DisplayName("고장난 엔진이면 자동차가 움직이지 않는다")
    void runBrokenEngineCar() {

        assemble(
                CarType.SEDAN,
                Engine.BROKEN,
                BrakeSystem.MANDO,
                SteeringSystem.BOSCH
        );

        carService.run(car);

        String result =
                output.toString();

        assertAll(
                () -> assertTrue(
                        result.contains(
                                "엔진이 고장나있습니다."
                        )
                ),

                () -> assertTrue(
                        result.contains(
                                "자동차가 움직이지 않습니다."
                        )
                )
        );
    }

    @Test
    @DisplayName("정상 부품 조합은 PASS")
    void testValidCar() {

        assemble(
                CarType.SUV,
                Engine.GM,
                BrakeSystem.MANDO,
                SteeringSystem.BOSCH
        );

        carService.test(car);

        assertTrue(
                output.toString()
                        .contains(
                                "자동차 부품 조합 테스트 결과 : PASS"
                        )
        );
    }

    @Test
    @DisplayName("잘못된 부품 조합은 FAIL")
    void testInvalidCar() {

        assemble(
                CarType.SUV,
                Engine.TOYOTA,
                BrakeSystem.MANDO,
                SteeringSystem.BOSCH
        );

        carService.test(car);

        String result =
                output.toString();

        assertAll(
                () -> assertTrue(
                        result.contains(
                                "자동차 부품 조합 테스트 결과 : FAIL"
                        )
                ),

                () -> assertTrue(
                        result.contains(
                                "SUV에는 TOYOTA 엔진 사용 불가"
                        )
                )
        );
    }
}