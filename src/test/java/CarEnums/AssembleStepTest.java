package CarEnums;

import CarAssembly.Car;
import CarAssembly.CarBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssembleStepTest {

    @Test
    @DisplayName("첫 단계(CAR_TYPE)는 뒤로 갈 수 없다")
    void firstStep_cannotGoBack() {
        assertThat(AssembleStep.CAR_TYPE.canGoBack()).isFalse();
    }

    @Test
    @DisplayName("첫 단계 이후 단계는 뒤로 갈 수 있고, previous()는 바로 이전 단계를 반환한다")
    void laterSteps_canGoBack_toPreviousStep() {
        assertThat(AssembleStep.ENGINE.canGoBack()).isTrue();
        assertThat(AssembleStep.ENGINE.previous()).isEqualTo(AssembleStep.CAR_TYPE);

        assertThat(AssembleStep.STEERING_SYSTEM.previous()).isEqualTo(AssembleStep.BRAKE_SYSTEM);
    }

    @Test
    @DisplayName("next()는 순서대로 다음 단계를 반환하고, 마지막 단계 다음은 null이다")
    void next_walksThroughAllSteps_andEndsWithNull() {
        assertThat(AssembleStep.CAR_TYPE.next()).isEqualTo(AssembleStep.ENGINE);
        assertThat(AssembleStep.ENGINE.next()).isEqualTo(AssembleStep.BRAKE_SYSTEM);
        assertThat(AssembleStep.BRAKE_SYSTEM.next()).isEqualTo(AssembleStep.STEERING_SYSTEM);
        assertThat(AssembleStep.STEERING_SYSTEM.next()).isNull();
    }

    @Test
    @DisplayName("0번 입력은 어느 단계에서나 '뒤로가기' 응답으로 인식된다")
    void isBackAnswer_true_onlyForZero() {
        assertThat(AssembleStep.ENGINE.isBackAnswer(0)).isTrue();
        assertThat(AssembleStep.ENGINE.isBackAnswer(1)).isFalse();
    }

    @Test
    @DisplayName("선택 가능 범위를 벗어난 입력은 isInSelectableRange()가 false다")
    void isInSelectableRange_rejectsOutOfRangeAnswers() {
        assertThat(AssembleStep.CAR_TYPE.isInSelectableRange(1)).isTrue();
        assertThat(AssembleStep.CAR_TYPE.isInSelectableRange(3)).isTrue();
        assertThat(AssembleStep.CAR_TYPE.isInSelectableRange(0)).isFalse();
        assertThat(AssembleStep.CAR_TYPE.isInSelectableRange(4)).isFalse();

        assertThat(AssembleStep.ENGINE.isInSelectableRange(4)).isTrue();
        assertThat(AssembleStep.ENGINE.isInSelectableRange(5)).isFalse();
    }

    @Test
    @DisplayName("CAR_TYPE 단계에서 select()는 CarBuilder에 CarType을 반영하고 라벨이 포함된 메시지를 반환한다")
    void select_onCarTypeStep_setsCarTypeAndReturnsMessage() {
        CarBuilder builder = new CarBuilder();

        String message = AssembleStep.CAR_TYPE.select(2, builder); // 2 -> SUV

        assertThat(message).isEqualTo("차량 타입으로 SUV을 선택하셨습니다.");
        // CarBuilder는 다른 필드가 없으므로 나머지를 채워 build()로 반영 여부를 검증한다
        builder.engine(Engine.GM);
        builder.brakeSystem(BrakeSystem.MANDO);
        builder.steeringSystem(SteeringSystem.BOSCH);
        Car car = builder.build();
        assertThat(car.getCarType()).isEqualTo(CarType.SUV);
    }

    @Test
    @DisplayName("ENGINE 단계에서 select()는 CarBuilder에 Engine을 반영한다")
    void select_onEngineStep_setsEngine() {
        CarBuilder builder = new CarBuilder();
        builder.carType(CarType.TRUCK);

        String message = AssembleStep.ENGINE.select(4, builder); // 4 -> 고장난 엔진

        assertThat(message).contains("고장난 엔진");
        builder.brakeSystem(BrakeSystem.BOSCH);
        builder.steeringSystem(SteeringSystem.BOSCH);
        assertThat(builder.build().getEngine()).isEqualTo(Engine.BROKEN);
    }

    @Test
    @DisplayName("각 단계의 메뉴 텍스트는 비어있지 않다")
    void everyStep_hasMenuLines() {
        for (AssembleStep step : AssembleStep.values()) {
            assertThat(step.menuLines()).isNotEmpty();
            assertThat(step.rangeErrorMessage()).isNotBlank();
        }
    }
}