package mission2.display;

import mission2.car.Car;
import mission2.car.part.Brakes;
import mission2.car.part.Part;
import mission2.car.part.CarTypes;
import mission2.car.part.Engines;
import mission2.car.part.SteeringSystems;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("자동차 화면 문구")
class CarViewTest {

    @Test
    @DisplayName("주행하면 사양을 보여 준다")
    void showsSpecWhenRunning() {
        assertThat(CarView.runLines(car(CarTypes.SUV, Engines.GM, Brakes.MANDO, SteeringSystems.MOBIS)))
                .containsExactly(
                        "Car Type : SUV",
                        "Engine   : GM",
                        "Brake    : Mando",
                        "Steering : Mobis",
                        "자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("쓸 수 없는 조합은 동작하지 않는다고 알린다")
    void showsBadCombination() {
        assertThat(CarView.runLines(car(CarTypes.SEDAN, Engines.GM, Brakes.CONTINENTAL, SteeringSystems.MOBIS)))
                .containsExactly("자동차가 동작되지 않습니다");
    }

    @Test
    @DisplayName("고장난 부품은 그 종류 이름으로 알린다")
    void showsBrokenPart() {
        assertThat(CarView.runLines(car(CarTypes.SEDAN, Engines.BROKEN, Brakes.MANDO, SteeringSystems.MOBIS)))
                .containsExactly("엔진이 고장나있습니다.", "자동차가 움직이지 않습니다.");
    }

    @Test
    @DisplayName("검사 결과는 PASS 나 FAIL 과 사유다")
    void showsVerdict() {
        assertThat(CarView.testLines(car(CarTypes.SEDAN, Engines.GM, Brakes.MANDO, SteeringSystems.MOBIS)))
                .containsExactly("자동차 부품 조합 테스트 결과 : PASS");
        assertThat(CarView.testLines(car(CarTypes.SEDAN, Engines.GM, Brakes.CONTINENTAL, SteeringSystems.MOBIS)))
                .containsExactly("자동차 부품 조합 테스트 결과 : FAIL", "Sedan에는 Continental제동장치 사용 불가");
    }

    private Car car(Part... parts) {
        Car car = new Car();
        for (Part part : parts) {
            car.install(part);
        }
        return car;
    }
}
