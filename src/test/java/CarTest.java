import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("자동차 부품 조합")
class CarTest {

    @Test
    @DisplayName("정상 조합은 문제가 없다")
    void goodCombinationHasNoProblem() {
        assertThat(car(Car.SEDAN, Car.GM, Car.MANDO, Car.MOBIS).findBadCombination()).isNull();
    }

    @ParameterizedTest(name = "{0} + {1} + {2} + {3}")
    @CsvSource({
            "Sedan, GM,     Continental, Mobis, Sedan에는 Continental제동장치 사용 불가",
            "SUV,   TOYOTA, Mando,       Mobis, SUV에는 TOYOTA엔진 사용 불가",
            "Truck, WIA,    Bosch,       Bosch, Truck에는 WIA엔진 사용 불가",
            "Truck, GM,     Mando,       Mobis, Truck에는 Mando제동장치 사용 불가",
            "Sedan, GM,     Bosch,       Mobis, Bosch제동장치에는 Bosch조향장치 이외 사용 불가"})
    @DisplayName("금지된 조합은 사유를 알려 준다")
    void badCombinationTellsReason(String type, String engine, String brake, String steering, String reason) {
        assertThat(car(type, engine, brake, steering).findBadCombination()).isEqualTo(reason);
    }

    @Test
    @DisplayName("Bosch 제동장치에 Bosch 조향장치는 쓸 수 있다")
    void boschPairIsAllowed() {
        assertThat(car(Car.SEDAN, Car.GM, Car.BOSCH, Car.BOSCH).findBadCombination()).isNull();
    }

    @Test
    @DisplayName("RUN 하면 사양을 보여 주고 주행한다")
    void runShowsSpec() {
        assertThat(car(Car.SUV, Car.GM, Car.MANDO, Car.MOBIS).perform(Car.RUN))
                .containsExactly(
                        "Car Type : SUV",
                        "Engine   : GM",
                        "Brake    : Mando",
                        "Steering : Mobis",
                        "자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("쓸 수 없는 조합은 RUN 해도 동작하지 않는다")
    void badCombinationDoesNotRun() {
        assertThat(car(Car.SEDAN, Car.GM, Car.CONTINENTAL, Car.MOBIS).perform(Car.RUN))
                .containsExactly("자동차가 동작되지 않습니다");
    }

    @Test
    @DisplayName("고장난 엔진은 조합 문제가 아니라 주행 문제다")
    void brokenEngineStopsTheCar() {
        Car car = car(Car.SEDAN, Car.BROKEN_ENGINE, Car.MANDO, Car.MOBIS);

        assertThat(car.findBadCombination()).isNull();
        assertThat(car.perform(Car.RUN)).containsExactly("엔진이 고장나있습니다.", "자동차가 움직이지 않습니다.");
    }

    @Test
    @DisplayName("TEST 는 PASS 나 FAIL 과 사유를 알려 준다")
    void testTellsVerdict() {
        assertThat(car(Car.SEDAN, Car.GM, Car.MANDO, Car.MOBIS).perform(Car.TEST))
                .containsExactly("자동차 부품 조합 테스트 결과 : PASS");
        assertThat(car(Car.SEDAN, Car.GM, Car.CONTINENTAL, Car.MOBIS).perform(Car.TEST))
                .containsExactly("자동차 부품 조합 테스트 결과 : FAIL", "Sedan에는 Continental제동장치 사용 불가");
    }

    private Car car(String type, String engine, String brake, String steering) {
        Car car = new Car();
        car.install(Car.CAR_TYPE, type);
        car.install(Car.ENGINE, engine);
        car.install(Car.BRAKE, brake);
        car.install(Car.STEERING, steering);
        return car;
    }
}
