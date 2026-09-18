package mission2.display;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("콘솔 화면")
class ConsoleDisplayTest {

    private final ByteArrayOutputStream printed = new ByteArrayOutputStream();

    @Test
    @DisplayName("입력을 한 줄 받고 앞뒤 공백을 지운다")
    void readsTrimmedLine() {
        assertThat(display("  2  ").ask()).isEqualTo("2");
    }

    @Test
    @DisplayName("입력이 끝나면 null 을 돌려준다")
    void returnsNullWhenInputEnds() {
        assertThat(display("").ask()).isNull();
    }

    @Test
    @DisplayName("물어볼 때 프롬프트를 띄운다")
    void showsPrompt() {
        display("1").ask();

        assertThat(screen()).contains("INPUT > ");
    }

    @Test
    @DisplayName("다시 그릴 때는 화면을 지우고 새로 쓴다")
    void clearsBeforeRedraw() {
        display("").redraw(List.of("첫 줄", "둘째 줄"));

        assertThat(screen()).startsWith("\033[H\033[2J").contains("첫 줄").contains("둘째 줄");
    }

    private ConsoleDisplay display(String input) {
        return new ConsoleDisplay(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(printed, true, StandardCharsets.UTF_8));
    }

    private String screen() {
        return printed.toString(StandardCharsets.UTF_8);
    }
}
