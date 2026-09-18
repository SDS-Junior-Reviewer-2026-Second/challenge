package CarAssembly;

import CarEnums.BrakeSystem;
import CarEnums.CarType;
import CarEnums.Engine;
import CarEnums.SteeringSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarBuilderTest {

    @Test
    @DisplayName("4개 부품을 모두 설정하면 해당 값으로 Car를 만든다")
    void build_createsCarWithSelectedParts() {
        CarBuilder builder = new CarBuilder();
        builder.carType(CarType.SEDAN);
        builder.engine(Engine.GM);
        builder.brakeSystem(BrakeSystem.MANDO);
        builder.steeringSystem(SteeringSystem.BOSCH);

        Car car = builder.build();

        assertThat(car.getCarType()).isEqualTo(CarType.SEDAN);
        assertThat(car.getEngine()).isEqualTo(Engine.GM);
        assertThat(car.getBrakeSystem()).isEqualTo(BrakeSystem.MANDO);
        assertThat(car.getSteeringSystem()).isEqualTo(SteeringSystem.BOSCH);
    }

    @Test
    @DisplayName("부품이 하나라도 비어있으면 build() 시 예외가 발생한다")
    void build_throws_whenAnyPartIsMissing() {
        CarBuilder builder = new CarBuilder();
        builder.carType(CarType.SEDAN);
        builder.engine(Engine.GM);
        builder.brakeSystem(BrakeSystem.MANDO);
        // steeringSystem을 설정하지 않음

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("아무것도 설정하지 않고 build() 하면 예외가 발생한다")
    void build_throws_whenNothingIsSelected() {
        CarBuilder builder = new CarBuilder();

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class);
    }
}