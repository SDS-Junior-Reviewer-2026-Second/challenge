package CarAssembly;

import CarEnums.BrakeSystem;
import CarEnums.CarType;
import CarEnums.Engine;
import CarEnums.SteeringSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {

    @Test
    @DisplayName("생성자로 전달한 값이 각 getter로 그대로 조회된다")
    void getters_returnConstructorValues() {
        Car car = new Car(CarType.SUV, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertThat(car.getCarType()).isEqualTo(CarType.SUV);
        assertThat(car.getEngine()).isEqualTo(Engine.GM);
        assertThat(car.getBrakeSystem()).isEqualTo(BrakeSystem.MANDO);
        assertThat(car.getSteeringSystem()).isEqualTo(SteeringSystem.BOSCH);
    }

    @Test
    @DisplayName("고장난 엔진이면 hasBrokenEngine()이 true다")
    void hasBrokenEngine_true_whenEngineIsBroken() {
        Car car = new Car(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertThat(car.hasBrokenEngine()).isTrue();
    }

    @Test
    @DisplayName("정상 엔진이면 hasBrokenEngine()이 false다")
    void hasBrokenEngine_false_whenEngineIsNormal() {
        Car car = new Car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);

        assertThat(car.hasBrokenEngine()).isFalse();
    }

    @Test
    @DisplayName("describe()는 4개 부품 정보를 정해진 형식으로 출력한다")
    void describe_containsAllPartsInExpectedFormat() {
        Car car = new Car(CarType.TRUCK, Engine.WIA, BrakeSystem.BOSCH, SteeringSystem.MOBIS);

        String expected = """
                Car Type : Truck
                Engine   : WIA
                Brake    : Bosch
                Steering : Mobis""";
        assertThat(car.describe()).isEqualTo(expected);
    }
}