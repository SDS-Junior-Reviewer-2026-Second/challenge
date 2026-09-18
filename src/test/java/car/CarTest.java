package car;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parts.BrakeSystem;
import parts.CarType;
import parts.Engine;
import parts.SteeringSystem;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {
    @Test
    @DisplayName("선택한 네 가지 부품을 차량에 저장한다")
    void storesSelectedParts() {
        Car car = car(2, 3, 1, 2);

        assertThat(car.getCarType()).isEqualTo(CarType.SUV);
        assertThat(car.getEngine()).isEqualTo(Engine.WIA);
        assertThat(car.getBrakeSystem()).isEqualTo(BrakeSystem.MANDO);
        assertThat(car.getSteeringSystem()).isEqualTo(SteeringSystem.MOBIS);
    }

    @Test
    @DisplayName("사용 가능한 조합은 정상 실행된다")
    void runsValidCombination() {
        Car car = car(2, 3, 2, 2);

        assertThat(car.isValid()).isTrue();
        assertThat(car.validationError()).isNull();
        assertThat(car.run()).isEqualTo(CarRunResult.SUCCESS);
    }

    @Test
    @DisplayName("고장난 엔진은 실행 결과로 구분한다")
    void reportsBrokenEngine() {
        assertThat(car(1, 4, 1, 1).run()).isEqualTo(CarRunResult.BROKEN_ENGINE);
    }

    @Test
    @DisplayName("Sedan에는 Continental 제동장치를 사용할 수 없다")
    void rejectsSedanAndContinental() {
        assertInvalid(1, 1, 2, 1, "Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    @DisplayName("SUV에는 Toyota 엔진을 사용할 수 없다")
    void rejectsSuvAndToyota() {
        assertInvalid(2, 2, 1, 1, "SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck에는 WIA 엔진을 사용할 수 없다")
    void rejectsTruckAndWia() {
        assertInvalid(3, 3, 3, 1, "Truck에는 WIA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck에는 Mando 제동장치를 사용할 수 없다")
    void rejectsTruckAndMando() {
        assertInvalid(3, 1, 1, 1, "Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    @DisplayName("Bosch 제동장치에는 Bosch 조향장치만 사용할 수 있다")
    void rejectsBoschBrakeAndNonBoschSteering() {
        assertInvalid(1, 1, 3, 2, "Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
    }

    private void assertInvalid(int type, int engine, int brake, int steering, String message) {
        Car car = car(type, engine, brake, steering);
        assertThat(car.isValid()).isFalse();
        assertThat(car.validationError()).isEqualTo(message);
        assertThat(car.run()).isEqualTo(CarRunResult.INVALID_COMBINATION);
    }

    private Car car(int type, int engine, int brake, int steering) {
        Car car = new Car();
        car.selectCarType(type);
        car.selectEngine(engine);
        car.selectBrakeSystem(brake);
        car.selectSteeringSystem(steering);
        return car;
    }
}
