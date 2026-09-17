package assemble.flow;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StepTest {

    @Test
    void nextWalksForwardAndStopsAtRunTest() {
        assertThat(Step.CAR_TYPE.next()).isEqualTo(Step.ENGINE);
        assertThat(Step.ENGINE.next()).isEqualTo(Step.BRAKE);
        assertThat(Step.BRAKE.next()).isEqualTo(Step.STEERING);
        assertThat(Step.STEERING.next()).isEqualTo(Step.RUN_TEST);
        assertThat(Step.RUN_TEST.next()).isEqualTo(Step.RUN_TEST);
    }

    @Test
    void backWalksBackwardAndRunTestReturnsToStart() {
        assertThat(Step.ENGINE.back()).isEqualTo(Step.CAR_TYPE);
        assertThat(Step.STEERING.back()).isEqualTo(Step.BRAKE);
        assertThat(Step.RUN_TEST.back()).isEqualTo(Step.CAR_TYPE);
        assertThat(Step.CAR_TYPE.back()).isEqualTo(Step.CAR_TYPE);
    }

    @Test
    void onlyFirstStepForbidsBack() {
        assertThat(Step.CAR_TYPE.allowsBack()).isFalse();
        assertThat(Step.ENGINE.allowsBack()).isTrue();
        assertThat(Step.RUN_TEST.allowsBack()).isTrue();
    }

    @Test
    void hasOptionMatchesMenuCodes() {
        assertThat(Step.CAR_TYPE.hasOption(3)).isTrue();
        assertThat(Step.CAR_TYPE.hasOption(4)).isFalse();
        assertThat(Step.ENGINE.hasOption(4)).isTrue();
        assertThat(Step.STEERING.hasOption(3)).isFalse();
        assertThat(Step.RUN_TEST.hasOption(2)).isTrue();
        assertThat(Step.RUN_TEST.hasOption(0)).isFalse();
    }
}
