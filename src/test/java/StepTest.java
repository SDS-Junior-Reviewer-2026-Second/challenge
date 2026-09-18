import org.junit.jupiter.api.Test;
import state.Step;

import static org.junit.jupiter.api.Assertions.*;

class StepTest {

    @Test
    void nextStep() {

        assertEquals(
                Step.ENGINE,
                Step.CAR_TYPE.next()
        );

        assertEquals(
                Step.BRAKE_SYSTEM,
                Step.ENGINE.next()
        );

        assertEquals(
                Step.STEERING_SYSTEM,
                Step.BRAKE_SYSTEM.next()
        );

        assertEquals(
                Step.RUN_TEST,
                Step.STEERING_SYSTEM.next()
        );
    }

    @Test
    void previousStep() {

        assertEquals(
                Step.CAR_TYPE,
                Step.ENGINE.prev()
        );

        assertEquals(
                Step.ENGINE,
                Step.BRAKE_SYSTEM.prev()
        );

        assertEquals(
                Step.BRAKE_SYSTEM,
                Step.STEERING_SYSTEM.prev()
        );

        assertEquals(
                Step.STEERING_SYSTEM,
                Step.RUN_TEST.prev()
        );
    }

    @Test
    void firstStepCannotGoFurtherBack() {

        assertEquals(
                Step.CAR_TYPE,
                Step.CAR_TYPE.prev()
        );
    }

    @Test
    void runTestNextStaysRunTest() {

        assertEquals(
                Step.RUN_TEST,
                Step.RUN_TEST.next()
        );
    }
}