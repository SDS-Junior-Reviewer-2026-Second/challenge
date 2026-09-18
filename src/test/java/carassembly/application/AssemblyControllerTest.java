package carassembly.application;

import carassembly.console.ConsoleView;
import carassembly.domain.BrakeSystem;
import carassembly.domain.CarType;
import carassembly.domain.Engine;
import carassembly.domain.RunStatus;
import carassembly.domain.SteeringSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntConsumer;

import static org.junit.jupiter.api.Assertions.*;

class AssemblyControllerTest {
    @ParameterizedTest(name = "[{index}] 콘솔 {0}/{1}/{2}/{3}")
    @CsvFileSource(resources = "/car-cases.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void 모든_조합의_RUN과_Test가_원본의_결과를_출력한다(
            CarType type, Engine engine, BrakeSystem brake, SteeringSystem steering,
            RunStatus status, String error) {
        String commands = String.format("%d\n%d\n%d\n%d\n1\n2\nexit\n",
                type.ordinal() + 1, engine.ordinal() + 1, brake.ordinal() + 1, steering.ordinal() + 1);
        Session result = runSession(commands);

        String expectedRun = switch (status) {
            case RUNNING -> "Car Type : " + type + "\nEngine   : " + engine
                    + "\nBrake    : " + brake.runName() + "\nSteering : " + steering.runName()
                    + "\n자동차가 동작됩니다.\n";
            case INCOMPATIBLE_PARTS -> "자동차가 동작되지 않습니다\n";
            case BROKEN_ENGINE -> "엔진이 고장나있습니다.\n자동차가 움직이지 않습니다.\n";
        };
        String expectedTest = error == null
                ? "자동차 부품 조합 테스트 결과 : PASS\n"
                : "자동차 부품 조합 테스트 결과 : FAIL\n" + error + "\n";
        assertTrue(result.output().contains(expectedRun), result.output());
        assertTrue(result.output().contains(expectedTest), result.output());
        assertEquals(1, occurrences(result.output(), "자동차 부품 조합 테스트 결과 : "));
        assertEquals(List.of(800, 800, 800, 800, 2000, 1500, 2000), result.pauses());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "", "1.5", "1 2", "2147483648", "-2147483649"})
    void 숫자가_아닌_입력_후에도_정상적으로_선택할_수_있다(String invalid) {
        Session result = runSession(invalid + "\n1\nexit\n");

        assertTrue(result.output().contains("ERROR :: 숫자만 입력 가능"));
        assertTrue(result.output().contains("차량 타입으로 Sedan을 선택하셨습니다."));
        assertEquals(2, occurrences(result.output(), "어떤 차량 타입을 선택할까요?"));
        assertEquals(List.of(800, 800), result.pauses());
    }

    @ParameterizedTest
    @CsvSource({"CAR_TYPE, 0", "CAR_TYPE, 4", "ENGINE, -1", "ENGINE, 5",
            "BRAKE_SYSTEM, -1", "BRAKE_SYSTEM, 4", "STEERING_SYSTEM, -1",
            "STEERING_SYSTEM, 3", "RUN_TEST, -1", "RUN_TEST, 3"})
    void 범위를_벗어난_입력은_현재_단계에_머문다(AssemblyStep step, int invalid) {
        String prefix = "1\n".repeat(step.ordinal());
        Session result = runSession(prefix + invalid + "\nexit\n");

        assertTrue(result.output().contains(step.errorMessage()));
        assertEquals(2, occurrences(result.output(), step.question()));
        assertEquals(step.ordinal() + 1, result.pauses().size());
    }

    @ParameterizedTest
    @CsvSource({"ENGINE, CAR_TYPE", "BRAKE_SYSTEM, ENGINE", "STEERING_SYSTEM, BRAKE_SYSTEM",
            "RUN_TEST, CAR_TYPE"})
    void 영을_누르면_정해진_이전_화면으로_간다(AssemblyStep step, AssemblyStep previous) {
        String prefix = "1\n".repeat(step.ordinal());
        Session result = runSession(prefix + "0\nexit\n");

        int zeroPrompt = result.output().lastIndexOf("INPUT > ", result.output().lastIndexOf("INPUT > ") - 1);
        String afterBack = result.output().substring(zeroPrompt);
        assertTrue(afterBack.contains(previous.question()), afterBack);
        assertEquals(step.ordinal(), result.pauses().size());
    }

    @Test
    void 뒤로가서_엔진을_바꾸면_새_선택이_적용된다() {
        Session result = runSession("2\n2\n0\n1\n1\n2\n1\n2\nexit\n");

        assertTrue(result.output().contains("Engine   : GM"));
        assertTrue(result.output().contains("자동차가 동작됩니다."));
        assertTrue(result.output().contains("자동차 부품 조합 테스트 결과 : PASS"));
    }

    @Test
    void 뒤로가서_제동장치를_바꾸면_새_선택이_적용된다() {
        Session result = runSession("1\n1\n2\n0\n1\n2\n1\nexit\n");

        assertTrue(result.output().contains("Brake    : Mando"));
        assertTrue(result.output().contains("자동차가 동작됩니다."));
    }

    @Test
    void 처음으로_돌아온_후에는_새_차량으로_조립한다() {
        Session result = runSession("1\n4\n1\n2\n1\n0\n3\n2\n2\n1\n1\nexit\n");

        assertTrue(result.output().contains("엔진이 고장나있습니다."));
        assertTrue(result.output().contains("Car Type : Truck\nEngine   : TOYOTA\n"
                + "Brake    : Continental\nSteering : Bosch\n자동차가 동작됩니다."));
    }

    @Test
    void RUN과_Test를_반복할_수_있다() {
        Session result = runSession("1\n1\n1\n1\n1\n2\n1\n2\nexit\n");

        assertEquals(2, occurrences(result.output(), "자동차가 동작됩니다."));
        assertEquals(2, occurrences(result.output(), "자동차 부품 조합 테스트 결과 : PASS"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void 모든_단계에서_대소문자와_공백이_있는_exit로_종료된다(int selections) {
        Session result = runSession("1\n".repeat(selections) + "  eXiT  \n2\n");

        assertTrue(result.output().endsWith("INPUT > 바이바이\n"));
        assertEquals(selections, result.pauses().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void 입력이_끝나면_예외_없이_종료된다(int selections) {
        Session result = assertDoesNotThrow(() -> runSession("1\n".repeat(selections)));

        assertTrue(result.output().endsWith("INPUT > "));
        assertFalse(result.output().contains("바이바이"));
    }

    @Test
    void 숫자_주변_공백을_허용한다() {
        Session result = runSession(" 1 \n 1 \n 1 \n 1 \n 1 \nexit\n");
        assertTrue(result.output().contains("자동차가 동작됩니다."));
    }

    @Test
    void 컨트롤러_객체끼리_선택_상태를_공유하지_않는다() {
        runSession("3\n2\n2\n1\n1\nexit\n");
        Session second = runSession("1\n1\n1\n2\n1\nexit\n");

        assertTrue(second.output().contains("Car Type : Sedan\nEngine   : GM\n"
                + "Brake    : Mando\nSteering : Mobis"));
        assertFalse(second.output().contains("Truck" + "\nEngine"));
    }

    @Test
    void 대기_중_인터럽트가_발생하면_다음_입력을_처리하지_않는다() {
        try {
            String output = runWithPause("1\n2\nexit\n", ignored -> Thread.currentThread().interrupt());
            assertTrue(Thread.currentThread().isInterrupted());
            assertFalse(output.contains("TOYOTA 엔진을 선택하셨습니다."));
            assertEquals(1, occurrences(output, "INPUT > "));
        } finally {
            Thread.interrupted(); // 다음 테스트에 인터럽트 상태를 남기지 않는다.
        }
    }

    private Session runSession(String commands) {
        List<Integer> pauses = new ArrayList<>();
        return new Session(runWithPause(commands, pauses::add), pauses);
    }

    private String runWithPause(String commands, IntConsumer pause) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner input = new Scanner(commands);
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            new AssemblyController(new ConsoleView(input, output), pause).run();
        }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private int occurrences(String value, String text) {
        return (value.length() - value.replace(text, "").length()) / text.length();
    }

    private record Session(String output, List<Integer> pauses) {}
}
