import org.example.assemble.AssemblyApplication;
import org.example.assemble.AssemblyStep;
import org.example.assemble.BrakeSystem;
import org.example.assemble.CarType;
import org.example.assemble.CompatibilityPolicy;
import org.example.assemble.CompatibilityResult;
import org.example.assemble.ConsoleUserInterface;
import org.example.assemble.Engine;
import org.example.assemble.SteeringSystem;
import org.example.assemble.VehicleConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssembleCharacterizationTest {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @ParameterizedTest(name = "car={0}, engine={1}, brake={2}, steering={3}")
    @MethodSource("allConfigurations")
    void preservesEveryCompatibilityCombination(
            int car, int engine, int brake, int steering,
            boolean expectedValid, String expectedTestOutput
    ) {
        VehicleConfiguration configuration = configuration(car, engine, brake, steering);
        CompatibilityResult result = new CompatibilityPolicy().evaluate(configuration);

        assertThat(result.valid()).isEqualTo(expectedValid);
        assertThat(renderTestResult(result)).isEqualTo(expectedTestOutput);
    }

    @Test
    void preservesRunOutputAndItsDecisionOrder() {
        assertThat(runApplication("1\n1\n1\n1\n1\nexit\n")).contains("""
                Car Type : Sedan
                Engine   : GM
                Brake    : Mando
                Steering : Bosch
                자동차가 동작됩니다.
                """);

        assertThat(runApplication("2\n4\n1\n2\n1\nexit\n")).contains("""
                엔진이 고장나있습니다.
                자동차가 움직이지 않습니다.
                """);

        String invalidBrokenCar = runApplication("1\n4\n2\n1\n1\nexit\n");
        assertThat(invalidBrokenCar).contains("자동차가 동작되지 않습니다\n");
        assertThat(invalidBrokenCar).doesNotContain("엔진이 고장나있습니다.");
    }

    @ParameterizedTest
    @MethodSource("rangeCases")
    void preservesRangeValidation(
            AssemblyStep step, int answer, boolean valid, String message
    ) {
        assertThat(step.accepts(answer)).isEqualTo(valid);

        if (!valid) {
            assertThat(renderRangeError(step)).isEqualTo(message);
        }
    }

    @Test
    void preservesSelectionMessages() {
        assertThat(renderSelection(CarType.TRUCK))
                .isEqualTo("차량 타입으로 Truck을 선택하셨습니다.\n");
        assertThat(renderSelection(Engine.BROKEN))
                .isEqualTo("고장난 엔진 엔진을 선택하셨습니다.\n");
        assertThat(renderSelection(BrakeSystem.CONTINENTAL))
                .isEqualTo("CONTINENTAL 제동장치를 선택하셨습니다.\n");
        assertThat(renderSelection(SteeringSystem.MOBIS))
                .isEqualTo("MOBIS 조향장치를 선택하셨습니다.\n");
    }

    @Test
    void preservesImmediateExitIncludingAnsiAndPrompt() {
        String output = runMain("  ExIt  \n");

        assertThat(output).isEqualTo(CLEAR_SCREEN + """
                        ______________
                       /|            |
                  ____/_|_____________|____
                 |                      O  |
                 '-(@)----------------(@)--'
                ===============================
                어떤 차량 타입을 선택할까요?
                1. Sedan
                2. SUV
                3. Truck
                ===============================
                INPUT > 바이바이
                """);
    }

    @Test
    void preservesACompleteSuccessfulJourney() {
        String output = runApplication("1\n1\n1\n1\n1\nexit\n");

        assertThat(output).containsSubsequence(
                "어떤 차량 타입을 선택할까요?", "차량 타입으로 Sedan을 선택하셨습니다.",
                "어떤 엔진을 탑재할까요?", "GM 엔진을 선택하셨습니다.",
                "어떤 제동장치를 선택할까요?", "MANDO 제동장치를 선택하셨습니다.",
                "어떤 조향장치를 선택할까요?", "BOSCH 조향장치를 선택하셨습니다.",
                "멋진 차량이 완성되었습니다.",
                "Car Type : Sedan", "Engine   : GM", "Brake    : Mando", "Steering : Bosch",
                "자동차가 동작됩니다.", "멋진 차량이 완성되었습니다.", "바이바이"
        );
        assertThat(countOccurrences(output, CLEAR_SCREEN)).isEqualTo(6);
    }

    @Test
    void supportsEveryBackNavigation() {
        String output = runApplication("""
                1
                0
                2
                1
                0
                3
                1
                0
                2
                1
                0
                exit
                """);

        assertThat(output).containsSubsequence(
                "어떤 차량 타입을 선택할까요?",
                "어떤 엔진을 탑재할까요?",
                "어떤 차량 타입을 선택할까요?",
                "어떤 엔진을 탑재할까요?",
                "어떤 제동장치를 선택할까요?",
                "어떤 엔진을 탑재할까요?",
                "어떤 제동장치를 선택할까요?",
                "어떤 조향장치를 선택할까요?",
                "어떤 제동장치를 선택할까요?",
                "어떤 조향장치를 선택할까요?",
                "멋진 차량이 완성되었습니다.",
                "어떤 차량 타입을 선택할까요?",
                "바이바이"
        );
    }

    @Test
    void reportsNonNumericAndOutOfRangeInputThenRetriesSameStep() {
        String output = runApplication("text\n0\n4\n1\nexit\n");

        assertThat(output).containsSubsequence(
                "ERROR :: 숫자만 입력 가능",
                "어떤 차량 타입을 선택할까요?",
                "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능",
                "어떤 차량 타입을 선택할까요?",
                "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능",
                "어떤 차량 타입을 선택할까요?",
                "차량 타입으로 Sedan을 선택하셨습니다.",
                "어떤 엔진을 탑재할까요?"
        );
    }

    private static Stream<Arguments> allConfigurations() {
        Stream.Builder<Arguments> cases = Stream.builder();
        for (int car = 1; car <= 3; car++) {
            for (int engine = 1; engine <= 4; engine++) {
                for (int brake = 1; brake <= 3; brake++) {
                    for (int steering = 1; steering <= 2; steering++) {
                        String failure = expectedFailure(car, engine, brake, steering);
                        boolean valid = failure == null;
                        String output = valid
                                ? "자동차 부품 조합 테스트 결과 : PASS\n"
                                : "자동차 부품 조합 테스트 결과 : FAIL\n" + failure + "\n";
                        cases.add(Arguments.of(car, engine, brake, steering, valid, output));
                    }
                }
            }
        }
        return cases.build();
    }

    private static String expectedFailure(int car, int engine, int brake, int steering) {
        if (car == 1 && brake == 2) return "Sedan에는 Continental제동장치 사용 불가";
        if (car == 2 && engine == 2) return "SUV에는 TOYOTA엔진 사용 불가";
        if (car == 3 && engine == 3) return "Truck에는 WIA엔진 사용 불가";
        if (car == 3 && brake == 1) return "Truck에는 Mando제동장치 사용 불가";
        if (brake == 3 && steering != 1) return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";
        return null;
    }

    private static Stream<Arguments> rangeCases() {
        return Stream.of(
                Arguments.of(AssemblyStep.CAR_TYPE, 0, false,
                        "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.CAR_TYPE, 1, true, ""),
                Arguments.of(AssemblyStep.CAR_TYPE, 3, true, ""),
                Arguments.of(AssemblyStep.CAR_TYPE, 4, false,
                        "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.ENGINE, -1, false,
                        "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.ENGINE, 0, true, ""),
                Arguments.of(AssemblyStep.ENGINE, 4, true, ""),
                Arguments.of(AssemblyStep.ENGINE, 5, false,
                        "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.BRAKE_SYSTEM, -1, false,
                        "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.BRAKE_SYSTEM, 0, true, ""),
                Arguments.of(AssemblyStep.BRAKE_SYSTEM, 3, true, ""),
                Arguments.of(AssemblyStep.BRAKE_SYSTEM, 4, false,
                        "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.STEERING_SYSTEM, -1, false,
                        "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.STEERING_SYSTEM, 0, true, ""),
                Arguments.of(AssemblyStep.STEERING_SYSTEM, 2, true, ""),
                Arguments.of(AssemblyStep.STEERING_SYSTEM, 3, false,
                        "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능\n"),
                Arguments.of(AssemblyStep.RUN_TEST, -1, false,
                        "ERROR :: Run 또는 Test 중 하나를 선택 필요\n"),
                Arguments.of(AssemblyStep.RUN_TEST, 0, true, ""),
                Arguments.of(AssemblyStep.RUN_TEST, 2, true, ""),
                Arguments.of(AssemblyStep.RUN_TEST, 3, false,
                        "ERROR :: Run 또는 Test 중 하나를 선택 필요\n")
        );
    }

    private static VehicleConfiguration configuration(
            int car, int engine, int brake, int steering
    ) {
        return new VehicleConfiguration(
                CarType.fromChoice(car),
                Engine.fromChoice(engine),
                BrakeSystem.fromChoice(brake),
                SteeringSystem.fromChoice(steering)
        );
    }

    private static String renderTestResult(CompatibilityResult result) {
        return captureUserInterfaceOutput(userInterface -> userInterface.showTestResult(result));
    }

    private static String renderRangeError(AssemblyStep step) {
        return captureUserInterfaceOutput(userInterface -> userInterface.showRangeError(step));
    }

    private static String renderSelection(CarType value) {
        return captureUserInterfaceOutput(userInterface -> userInterface.showSelection(value));
    }

    private static String renderSelection(Engine value) {
        return captureUserInterfaceOutput(userInterface -> userInterface.showSelection(value));
    }

    private static String renderSelection(BrakeSystem value) {
        return captureUserInterfaceOutput(userInterface -> userInterface.showSelection(value));
    }

    private static String renderSelection(SteeringSystem value) {
        return captureUserInterfaceOutput(userInterface -> userInterface.showSelection(value));
    }

    private static String captureUserInterfaceOutput(UserInterfaceAction action) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ConsoleUserInterface userInterface = consoleUserInterface("", output)) {
            action.run(userInterface);
        }
        return normalize(output.toString(StandardCharsets.UTF_8));
    }

    private static String runApplication(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ConsoleUserInterface userInterface = consoleUserInterface(input, output)) {
            new AssemblyApplication(userInterface, new CompatibilityPolicy(), ignored -> { }).run();
        }
        return normalize(output.toString(StandardCharsets.UTF_8));
    }

    private static ConsoleUserInterface consoleUserInterface(
            String input, ByteArrayOutputStream output
    ) {
        return new ConsoleUserInterface(
                new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8),
                new PrintStream(output, true, StandardCharsets.UTF_8)
        );
    }

    private static String runMain(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        Assemble.main(new String[0]);
        return normalize(output.toString(StandardCharsets.UTF_8));
    }

    private static int countOccurrences(String value, String token) {
        return (value.length() - value.replace(token, "").length()) / token.length();
    }

    private static String normalize(String output) {
        return output.replace("\r\n", "\n");
    }

    @FunctionalInterface
    private interface UserInterfaceAction {
        void run(ConsoleUserInterface userInterface);
    }
}
