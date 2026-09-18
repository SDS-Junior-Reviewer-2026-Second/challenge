package carassembly.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {
    // 기대값은 새 구현이 아닌 첨부 원본을 실행해서 얻은 72개 조합의 결과다.
    @ParameterizedTest(name = "[{index}] {0}/{1}/{2}/{3}")
    @CsvFileSource(resources = "/car-cases.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void 모든_부품_조합의_결과가_원본과_같다(
            CarType type, Engine engine, BrakeSystem brake, SteeringSystem steering,
            RunStatus expectedStatus, String expectedError) {
        Car car = new Car(type, engine, brake, steering);

        assertAll(
                () -> assertEquals(Optional.ofNullable(expectedError), car.findCompatibilityError()),
                () -> assertEquals(expectedStatus, car.runStatus())
        );
    }

    @ParameterizedTest
    @CsvSource({
            "SEDAN, GM, CONTINENTAL, BOSCH, Sedan에는 Continental제동장치 사용 불가",
            "SUV, TOYOTA, MANDO, BOSCH, SUV에는 TOYOTA엔진 사용 불가",
            "TRUCK, WIA, CONTINENTAL, BOSCH, Truck에는 WIA엔진 사용 불가",
            "TRUCK, GM, MANDO, BOSCH, Truck에는 Mando제동장치 사용 불가",
            "SUV, GM, BOSCH, MOBIS, Bosch제동장치에는 Bosch조향장치 이외 사용 불가"
    })
    void 각각의_호환성_규칙을_검사한다(
            CarType type, Engine engine, BrakeSystem brake, SteeringSystem steering,
            String expectedError) {
        Car car = new Car(type, engine, brake, steering);

        assertEquals(Optional.of(expectedError), car.findCompatibilityError());
        assertEquals(RunStatus.INCOMPATIBLE_PARTS, car.runStatus());
    }

    @Test
    void 여러_규칙을_위반하면_원본의_첫번째_오류를_반환한다() {
        Car car = new Car(CarType.TRUCK, Engine.WIA, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertEquals(Optional.of("Truck에는 WIA엔진 사용 불가"), car.findCompatibilityError());
    }

    @Test
    void 고장난_엔진도_호환성_검사는_통과할_수_있다() {
        Car car = new Car(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.MOBIS);

        assertTrue(car.findCompatibilityError().isEmpty());
        assertEquals(RunStatus.BROKEN_ENGINE, car.runStatus());
    }

    @Test
    void 호환성_오류가_엔진_고장보다_우선한다() {
        Car car = new Car(CarType.SEDAN, Engine.BROKEN, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);

        assertEquals(RunStatus.INCOMPATIBLE_PARTS, car.runStatus());
    }

    @Test
    void 부품이_하나라도_빠진_차량은_만들_수_없다() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new Car(null, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Car(CarType.SEDAN, null, BrakeSystem.MANDO, SteeringSystem.BOSCH)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Car(CarType.SEDAN, Engine.GM, null, SteeringSystem.BOSCH)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, null))
        );
    }
}
