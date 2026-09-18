package CarAssembly;

import CarEnums.CarType;
import CarEnums.Engine;
import CarEnums.SteeringSystem;
import CarEnums.BrakeSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssemblyProgressTest {

    @Test
    @DisplayName("초기 상태는 CAR_TYPE 단계, 미완료, 뒤로가기 불가다")
    void initialState() {
        AssemblyProgress progress = new AssemblyProgress();

        assertThat(progress.isCompleted()).isFalse();
        assertThat(progress.canGoBack()).isFalse();
    }

    @Test
    @DisplayName("selectAndAdvance()는 CarBuilder에 값을 반영하고 다음 단계로 넘어간다")
    void selectAndAdvance_movesToNextStep() {
        AssemblyProgress progress = new AssemblyProgress();

        String message = progress.selectAndAdvance(1); // CAR_TYPE 1 -> Sedan

        assertThat(message).contains("Sedan");
        assertThat(progress.isCompleted()).isFalse();
        assertThat(progress.canGoBack()).isTrue();
    }

    @Test
    @DisplayName("모든 단계를 마치면 completed가 true가 되고 build() 가능한 상태가 된다")
    void selectAndAdvance_completesAfterLastStep() {
        AssemblyProgress progress = new AssemblyProgress();

        progress.selectAndAdvance(1); // CAR_TYPE -> Sedan
        progress.selectAndAdvance(1); // ENGINE -> GM
        progress.selectAndAdvance(1); // BRAKE_SYSTEM -> MANDO
        progress.selectAndAdvance(1); // STEERING_SYSTEM -> BOSCH

        assertThat(progress.isCompleted()).isTrue();
        Car car = progress.carBuilder().build();
        assertThat(car.getCarType()).isEqualTo(CarType.SEDAN);
        assertThat(car.getEngine()).isEqualTo(Engine.GM);
        assertThat(car.getBrakeSystem()).isEqualTo(BrakeSystem.MANDO);
        assertThat(car.getSteeringSystem()).isEqualTo(SteeringSystem.BOSCH);
    }

    @Test
    @DisplayName("goToPreviousStep()은 바로 이전 단계로 되돌린다")
    void goToPreviousStep_returnsToEarlierStep() {
        AssemblyProgress progress = new AssemblyProgress();
        progress.selectAndAdvance(1); // CAR_TYPE -> ENGINE

        progress.goToPreviousStep();

        assertThat(progress.currentStep().menuLines()).isNotEmpty();
        assertThat(progress.canGoBack()).isFalse(); // 다시 CAR_TYPE(첫 단계)으로 돌아왔다
    }

    @Test
    @DisplayName("reset()은 처음 상태로 되돌린다")
    void reset_returnsToInitialState() {
        AssemblyProgress progress = new AssemblyProgress();
        progress.selectAndAdvance(1);
        progress.selectAndAdvance(1);

        progress.reset();

        assertThat(progress.isCompleted()).isFalse();
        assertThat(progress.canGoBack()).isFalse();
    }
}