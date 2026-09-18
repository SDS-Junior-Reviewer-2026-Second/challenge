package assemble;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class AssembleIntegrationTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void redirectOutput() {
        originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("exit 입력은 프로그램을 종료한다")
    void exitsProgram() {
        run("exit\n");

        assertThat(console()).contains("어떤 차량 타입을 선택할까요?", "INPUT > ", "바이바이");
    }

    @Test
    @DisplayName("숫자가 아닌 입력과 범위를 벗어난 입력을 안내하고 다시 받는다")
    void retriesInvalidInput() {
        run("abc\n0\nexit\n");

        assertThat(console()).contains("ERROR :: 숫자만 입력 가능", "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
    }

    @Test
    @DisplayName("네 가지 부품을 순서대로 선택하고 완성 화면에서 처음으로 돌아간다")
    void completesAssemblyAndReturnsToFirstStep() {
        run("1\n1\n1\n1\n0\nexit\n");

        assertThat(console()).contains(
                "차량 타입으로 Sedan을 선택하셨습니다.",
                "GM 엔진을 선택하셨습니다.",
                "Mando 제동장치를 선택하셨습니다.",
                "Bosch 조향장치를 선택하셨습니다.",
                "멋진 차량이 완성되었습니다.",
                "0. 처음 화면으로 돌아가기",
                "바이바이");
        assertThat(count(console(), "어떤 차량 타입을 선택할까요?")).isEqualTo(2);
    }

    @Test
    @DisplayName("부품 선택 단계에서 0을 입력하면 이전 단계로 돌아간다")
    void movesBackFromPartStep() {
        run("1\n0\nexit\n");

        assertThat(count(console(), "어떤 차량 타입을 선택할까요?")).isEqualTo(2);
        assertThat(console()).contains("어떤 엔진을 탑재할까요?");
    }

    private void run(String input) {
        new Assemble(new Scanner(input)).run();
    }

    private int count(String text, String target) {
        return (text.length() - text.replace(target, "").length()) / target.length();
    }

    private String console() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
