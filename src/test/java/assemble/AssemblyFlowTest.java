package assemble;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssemblyFlowTest {
    @Test
    @DisplayName("조립 단계는 정의된 순서대로 이동하고 마지막에서 멈춘다")
    void movesForwardInOrder() {
        AssemblyFlow flow = new AssemblyFlow();

        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.CAR_TYPE);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.ENGINE);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.BRAKE_SYSTEM);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.STEERING_SYSTEM);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.RUN_TEST);
        flow.moveNext();
        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.RUN_TEST);
    }

    @Test
    @DisplayName("부품 단계의 뒤로가기는 이전 단계로 이동한다")
    void movesToPreviousPartStep() {
        AssemblyFlow flow = new AssemblyFlow();
        flow.moveNext();
        flow.moveNext();
        flow.moveBack();

        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.ENGINE);
    }

    @Test
    @DisplayName("완성 단계의 뒤로가기는 첫 단계로 이동한다")
    void movesFromRunTestToFirstStep() {
        AssemblyFlow flow = new AssemblyFlow();
        for (int i = 0; i < 4; i++) {
            flow.moveNext();
        }
        flow.moveBack();

        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.CAR_TYPE);
    }

    @Test
    @DisplayName("첫 단계에서는 더 뒤로 이동하지 않는다")
    void staysAtFirstStep() {
        AssemblyFlow flow = new AssemblyFlow();
        flow.moveBack();

        assertThat(flow.currentStep()).isEqualTo(AssemblyStep.CAR_TYPE);
        assertThat(BackNavigation.PREVIOUS.targetIndex(0)).isZero();
        assertThat(BackNavigation.PREVIOUS.targetIndex(3)).isEqualTo(2);
        assertThat(BackNavigation.FIRST.targetIndex(4)).isZero();
    }
}
