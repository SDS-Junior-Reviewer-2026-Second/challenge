import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

/** End-to-end tests driving the wizard through scripted stdin, mirroring the old main() input scripts. */
class AssemblyWizardTest {

    private static String runWizard(String stdinScript) throws Exception {
        Scanner scanner = new Scanner(new ByteArrayInputStream(stdinScript.getBytes(StandardCharsets.UTF_8)));
        return ConsoleCapture.captureStdOut(() -> new AssemblyWizard(scanner).run());
    }

    @Test
    void exitCommand_printsGoodbyeAndStops() throws Exception {
        String output = runWizard("exit\n");
        assertThat(output).contains("바이바이");
    }

    @Test
    void nonNumericInput_showsErrorAndRetries() throws Exception {
        String output = runWizard("abc\nexit\n");
        assertThat(output).contains("ERROR :: 숫자만 입력 가능").contains("바이바이");
    }

    @Test
    void outOfRangeCarType_showsErrorAndRetries() throws Exception {
        String output = runWizard("9\nexit\n");
        assertThat(output).contains("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능").contains("바이바이");
    }

    @Test
    void backNavigation_returnsToPreviousStep() throws Exception {
        String output = runWizard("1\n0\nexit\n");
        assertThat(output)
                .contains("차량 타입으로 Sedan을 선택하셨습니다.")
                .contains("어떤 엔진을 탑재할까요?")
                .contains("바이바이");
    }

    @Test
    void fullWalkthrough_runsAndTestsThenReturnsToStart() throws Exception {
        // CarType=Sedan, Engine=GM, Brake=MANDO, Steering=BOSCH -> Run_Test menu:
        // invalid answer, then RUN, then TEST, then back to CarType_Q, then exit.
        String output = runWizard("1\n1\n1\n1\n9\n1\n2\n0\nexit\n");
        assertThat(output)
                .contains("차량 타입으로 Sedan을 선택하셨습니다.")
                .contains("GM 엔진을 선택하셨습니다.")
                .contains("MANDO 제동장치를 선택하셨습니다.")
                .contains("BOSCH 조향장치를 선택하셨습니다.")
                .contains("ERROR :: Run 또는 Test 중 하나를 선택 필요")
                .contains("Car Type : Sedan")
                .contains("자동차가 동작됩니다.")
                .contains("자동차 부품 조합 테스트 결과 : PASS")
                .contains("어떤 차량 타입을 선택할까요?")
                .contains("바이바이");
    }
}
