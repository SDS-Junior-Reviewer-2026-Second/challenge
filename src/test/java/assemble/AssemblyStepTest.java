package assemble;

import assemble.ui.StepAction;
import car.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parts.BrakeSystem;
import parts.CarType;
import parts.Engine;
import parts.SteeringSystem;

import static org.assertj.core.api.Assertions.assertThat;

class AssemblyStepTest {
    @Test
    @DisplayName("각 부품 단계는 UI 검증과 선택 실행을 위임한다")
    void delegatesPartStepBehavior() {
        Car car = new Car();

        assertThat(AssemblyStep.CAR_TYPE.execute(car, 1)).isEqualTo(StepAction.NEXT);
        assertThat(AssemblyStep.ENGINE.execute(car, 1)).isEqualTo(StepAction.NEXT);
        assertThat(AssemblyStep.BRAKE_SYSTEM.execute(car, 1)).isEqualTo(StepAction.NEXT);
        assertThat(AssemblyStep.STEERING_SYSTEM.execute(car, 1)).isEqualTo(StepAction.NEXT);

        assertThat(car.getCarType()).isEqualTo(CarType.SEDAN);
        assertThat(car.getEngine()).isEqualTo(Engine.GM);
        assertThat(car.getBrakeSystem()).isEqualTo(BrakeSystem.MANDO);
        assertThat(car.getSteeringSystem()).isEqualTo(SteeringSystem.BOSCH);
    }

    @Test
    @DisplayName("각 단계는 자신의 입력 범위와 뒤로가기 정책을 가진다")
    void exposesValidationAndNavigationPolicy() {
        assertThat(AssemblyStep.CAR_TYPE.isValidInput(0)).isFalse();
        assertThat(AssemblyStep.ENGINE.isValidInput(0)).isTrue();
        assertThat(AssemblyStep.BRAKE_SYSTEM.isValidInput(4)).isFalse();
        assertThat(AssemblyStep.STEERING_SYSTEM.isValidInput(2)).isTrue();
        assertThat(AssemblyStep.RUN_TEST.isValidInput(2)).isTrue();

        assertThat(AssemblyStep.CAR_TYPE.validationError()).contains("차량 타입은 1 ~ 3");
        assertThat(AssemblyStep.RUN_TEST.validationError()).isEqualTo("ERROR :: Run 또는 Test 중 하나를 선택 필요");
        assertThat(AssemblyStep.CAR_TYPE.getBackNavigation()).isEqualTo(BackNavigation.PREVIOUS);
        assertThat(AssemblyStep.RUN_TEST.getBackNavigation()).isEqualTo(BackNavigation.FIRST);
    }
}
