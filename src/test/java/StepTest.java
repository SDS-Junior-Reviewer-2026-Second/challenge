import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class StepTest {

    // ---- showMenu(): static menu text ----

    private record MenuCase(String description, Step step, String[] mustContain) {}

    private static Stream<MenuCase> menuCases() {
        return Stream.of(
                new MenuCase("CAR_TYPE prints car type options", Step.CAR_TYPE,
                        new String[]{"어떤 차량 타입을 선택할까요?", "1. Sedan", "2. SUV", "3. Truck"}),
                new MenuCase("ENGINE prints engine options", Step.ENGINE,
                        new String[]{"어떤 엔진을 탑재할까요?", "1. GM", "2. TOYOTA", "3. WIA", "4. 고장난 엔진"}),
                new MenuCase("BRAKE prints brake options", Step.BRAKE,
                        new String[]{"어떤 제동장치를 선택할까요?", "1. MANDO", "2. CONTINENTAL", "3. BOSCH"}),
                new MenuCase("STEERING prints steering options", Step.STEERING,
                        new String[]{"어떤 조향장치를 선택할까요?", "1. BOSCH", "2. MOBIS"}),
                new MenuCase("RUN_TEST prints run/test options", Step.RUN_TEST,
                        new String[]{"멋진 차량이 완성되었습니다.", "1. RUN", "2. Test"})
        );
    }

    @TestFactory
    Stream<DynamicTest> showMenu_printsExpectedMenuText() {
        return menuCases().map(tc -> dynamicTest(tc.description(), () -> {
            String output = ConsoleCapture.captureStdOut(tc.step()::showMenu);
            assertThat(output).contains(tc.mustContain());
        }));
    }

    // ---- isValidAnswer(): per-step input boundaries ----

    private record BoundaryCase(String description, Step step, int answer, boolean expected) {}

    private static Stream<BoundaryCase> boundaryCases(String stepName, Step step, int min, int max) {
        return Stream.of(
                new BoundaryCase(stepName + " below min", step, min - 1, false),
                new BoundaryCase(stepName + " at min", step, min, true),
                new BoundaryCase(stepName + " at max", step, max, true),
                new BoundaryCase(stepName + " above max", step, max + 1, false)
        );
    }

    private static Stream<BoundaryCase> isValidAnswerCases() {
        return Stream.of(
                boundaryCases("CAR_TYPE", Step.CAR_TYPE, 1, 3),
                boundaryCases("ENGINE", Step.ENGINE, 0, 4),
                boundaryCases("BRAKE", Step.BRAKE, 0, 3),
                boundaryCases("STEERING", Step.STEERING, 0, 2),
                boundaryCases("RUN_TEST", Step.RUN_TEST, 0, 2)
        ).flatMap(s -> s);
    }

    @TestFactory
    Stream<DynamicTest> isValidAnswer_boundaryInput_matchesExpectedValidity() {
        return isValidAnswerCases().map(tc -> dynamicTest(tc.description(), () ->
                assertThat(tc.step().isValidAnswer(tc.answer())).isEqualTo(tc.expected())));
    }

    // ---- apply(): assigns Car field + prints selection message ----

    private record ApplyCase(String description, Step step, int answer, String expectedMessage) {}

    private static Stream<ApplyCase> applyCases() {
        return Stream.of(
                new ApplyCase("CAR_TYPE(1) -> Sedan", Step.CAR_TYPE, 1, "차량 타입으로 Sedan을 선택하셨습니다."),
                new ApplyCase("CAR_TYPE(2) -> SUV", Step.CAR_TYPE, 2, "차량 타입으로 SUV을 선택하셨습니다."),
                new ApplyCase("CAR_TYPE(3) -> Truck", Step.CAR_TYPE, 3, "차량 타입으로 Truck을 선택하셨습니다."),
                new ApplyCase("ENGINE(1) -> GM", Step.ENGINE, 1, "GM 엔진을 선택하셨습니다."),
                new ApplyCase("ENGINE(4) -> 고장난 엔진", Step.ENGINE, 4, "고장난 엔진 엔진을 선택하셨습니다."),
                new ApplyCase("BRAKE(2) -> CONTINENTAL", Step.BRAKE, 2, "CONTINENTAL 제동장치를 선택하셨습니다."),
                new ApplyCase("STEERING(2) -> MOBIS", Step.STEERING, 2, "MOBIS 조향장치를 선택하셨습니다.")
        );
    }

    @TestFactory
    Stream<DynamicTest> apply_assemblyStep_assignsCarFieldAndPrintsMessage() {
        return applyCases().map(tc -> dynamicTest(tc.description(), () -> {
            Car car = new Car();
            String output = ConsoleCapture.captureStdOut(() -> tc.step().apply(car, tc.answer()));
            assertThat(output).contains(tc.expectedMessage());
        }));
    }

    // ---- next/previous step transitions ----

    @TestFactory
    Stream<DynamicTest> nextStep_advancesThroughAssemblySteps() {
        return Stream.of(
                dynamicTest("CAR_TYPE -> ENGINE", () -> assertThat(Step.CAR_TYPE.nextStep()).isEqualTo(Step.ENGINE)),
                dynamicTest("ENGINE -> BRAKE", () -> assertThat(Step.ENGINE.nextStep()).isEqualTo(Step.BRAKE)),
                dynamicTest("BRAKE -> STEERING", () -> assertThat(Step.BRAKE.nextStep()).isEqualTo(Step.STEERING)),
                dynamicTest("STEERING -> RUN_TEST", () -> assertThat(Step.STEERING.nextStep()).isEqualTo(Step.RUN_TEST)),
                dynamicTest("RUN_TEST -> RUN_TEST (stays)", () -> assertThat(Step.RUN_TEST.nextStep()).isEqualTo(Step.RUN_TEST))
        );
    }

    @TestFactory
    Stream<DynamicTest> previousStep_walksBackward_exceptRunTestWhichRestarts() {
        return Stream.of(
                dynamicTest("ENGINE -> CAR_TYPE", () -> assertThat(Step.ENGINE.previousStep()).isEqualTo(Step.CAR_TYPE)),
                dynamicTest("BRAKE -> ENGINE", () -> assertThat(Step.BRAKE.previousStep()).isEqualTo(Step.ENGINE)),
                dynamicTest("STEERING -> BRAKE", () -> assertThat(Step.STEERING.previousStep()).isEqualTo(Step.BRAKE)),
                dynamicTest("RUN_TEST -> CAR_TYPE (restart, not STEERING)",
                        () -> assertThat(Step.RUN_TEST.previousStep()).isEqualTo(Step.CAR_TYPE))
        );
    }

    @Test
    void appliesOwnDelay_trueOnlyForRunTest() {
        assertThat(Step.CAR_TYPE.appliesOwnDelay()).isFalse();
        assertThat(Step.ENGINE.appliesOwnDelay()).isFalse();
        assertThat(Step.BRAKE.appliesOwnDelay()).isFalse();
        assertThat(Step.STEERING.appliesOwnDelay()).isFalse();
        assertThat(Step.RUN_TEST.appliesOwnDelay()).isTrue();
    }
}
