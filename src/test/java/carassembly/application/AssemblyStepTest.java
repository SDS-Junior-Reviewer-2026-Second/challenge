package carassembly.application;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AssemblyStepTest {
    @ParameterizedTest
    @CsvSource({
            "CAR_TYPE, 1, 3",
            "ENGINE, 0, 4",
            "BRAKE_SYSTEM, 0, 3",
            "STEERING_SYSTEM, 0, 2",
            "RUN_TEST, 0, 2"
    })
    void 각_단계의_입력_경계를_검증한다(AssemblyStep step, int minimum, int maximum) {
        for (int answer = minimum; answer <= maximum; answer++) {
            assertTrue(step.accepts(answer), "유효한 입력: " + answer);
        }
        assertFalse(step.accepts(minimum - 1));
        assertFalse(step.accepts(maximum + 1));
        assertFalse(step.accepts(Integer.MIN_VALUE));
        assertFalse(step.accepts(Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @CsvSource({
            "CAR_TYPE, ENGINE, CAR_TYPE",
            "ENGINE, BRAKE_SYSTEM, CAR_TYPE",
            "BRAKE_SYSTEM, STEERING_SYSTEM, ENGINE",
            "STEERING_SYSTEM, RUN_TEST, BRAKE_SYSTEM",
            "RUN_TEST, RUN_TEST, CAR_TYPE"
    })
    void 단계별_진행과_뒤로가기가_정확하다(
            AssemblyStep step, AssemblyStep next, AssemblyStep previous) {
        assertEquals(next, step.next());
        assertEquals(previous, step.previous());
    }
}
