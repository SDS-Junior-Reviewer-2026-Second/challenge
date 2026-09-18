package mission2.car;

import mission2.car.part.Brakes;
import mission2.car.part.CarTypes;
import mission2.car.part.Engines;
import mission2.car.part.Part;
import mission2.car.part.SteeringSystems;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("자동차")
class CarTest {

    @Test
    @DisplayName("정상 조합은 조합 문제도 고장도 없다")
    void goodCombinationHasNoProblem() {
        Car car = car(CarTypes.SEDAN, Engines.GM, Brakes.MANDO, SteeringSystems.MOBIS);

        assertThat(car.findBadCombination()).isEmpty();
        assertThat(car.findBrokenCategory()).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badCombinations")
    @DisplayName("금지된 조합은 사유를 알려 준다")
    void badCombinationTellsReason(String name, Car car, String reason) {
        assertThat(car.findBadCombination()).contains(reason);
    }

    static Stream<Object[]> badCombinations() {
        return Stream.of(
                new Object[]{"Sedan + Continental",
                        car(CarTypes.SEDAN, Engines.GM, Brakes.CONTINENTAL, SteeringSystems.MOBIS),
                        "Sedan에는 Continental제동장치 사용 불가"},
                new Object[]{"SUV + TOYOTA",
                        car(CarTypes.SUV, Engines.TOYOTA, Brakes.MANDO, SteeringSystems.MOBIS),
                        "SUV에는 TOYOTA엔진 사용 불가"},
                new Object[]{"Truck + WIA",
                        car(CarTypes.TRUCK, Engines.WIA, Brakes.BOSCH, SteeringSystems.BOSCH),
                        "Truck에는 WIA엔진 사용 불가"},
                new Object[]{"Truck + Mando",
                        car(CarTypes.TRUCK, Engines.GM, Brakes.MANDO, SteeringSystems.MOBIS),
                        "Truck에는 Mando제동장치 사용 불가"},
                new Object[]{"Bosch 제동장치 + Mobis 조향장치",
                        car(CarTypes.SEDAN, Engines.GM, Brakes.BOSCH, SteeringSystems.MOBIS),
                        "Bosch제동장치에는 Bosch조향장치 이외 사용 불가"});
    }

    @Test
    @DisplayName("Bosch 제동장치에 Bosch 조향장치는 쓸 수 있다")
    void boschPairIsAllowed() {
        assertThat(car(CarTypes.SEDAN, Engines.GM, Brakes.BOSCH, SteeringSystems.BOSCH)
                .findBadCombination()).isEmpty();
    }

    @Test
    @DisplayName("고장난 부품이 있으면 어느 자리인지 알려 준다")
    void tellsWhichCategoryIsBroken() {
        Car car = car(CarTypes.SEDAN, Engines.BROKEN, Brakes.MANDO, SteeringSystems.MOBIS);

        assertThat(car.findBadCombination()).isEmpty();
        assertThat(car.findBrokenCategory()).contains(Engines.CATEGORY);
    }

    @Test
    @DisplayName("이름이 같아도 분류가 다르면 다른 부품이다")
    void sameNameInAnotherCategoryIsAnotherPart() {
        Car car = new Car();
        car.install(Brakes.BOSCH);

        assertThat(car.has(Brakes.BOSCH)).isTrue();
        assertThat(car.has(SteeringSystems.BOSCH)).isFalse();
    }

    @Test
    @DisplayName("끼운 부품 이름을 자리별로 알려 준다")
    void tellsInstalledPartName() {
        Car car = new Car();
        car.install(Engines.GM);

        assertThat(car.partNameOf(Engines.CATEGORY)).isEqualTo("GM");
        assertThat(car.partNameOf(Brakes.CATEGORY)).isEmpty();
    }

    private static Car car(Part... parts) {
        Car car = new Car();
        for (Part part : parts) {
            car.install(part);
        }
        return car;
    }
}
