package mission2.legacy;

import mission2.Assemble;
import mission2.display.ConsoleDisplay;
import mission2.display.Display;
import mission2.display.step.StepFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("원본 프로그램")
class LegacyParityTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreConsole() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    @DisplayName("같은 입력에 원본과 한 글자도 다르지 않은 화면을 보여 준다")
    void showsExactlyTheSameScreen(String name, String script) {
        assertThat(refactoredOutput(script)).isEqualTo(legacyOutput(script));
    }

    static Stream<Arguments> scenarios() {
        return Stream.of(
                arguments("정상 조립 후 RUN", "1\n1\n1\n2\n1\nexit\n"),
                arguments("금지된 조합으로 Test", "1\n1\n2\n2\n2\nexit\n"),
                arguments("고장난 엔진으로 RUN", "1\n4\n1\n2\n1\nexit\n"),
                arguments("잘못된 입력과 뒤로가기", "abc\n9\n1\n0\n1\n1\n0\n2\nexit\n"));
    }

    private String legacyOutput(String script) {
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(script.getBytes(UTF_8)));
        System.setOut(new PrintStream(printed, true, UTF_8));
        LegacyAssemble.main(new String[0]);
        return screen(printed);
    }

    private String refactoredOutput(String script) {
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        new Assemble(instantDisplay(script, printed), StepFactory.assemblyLine()).run();
        return screen(printed);
    }

    private Display instantDisplay(String script, ByteArrayOutputStream printed) {
        return new ConsoleDisplay(new ByteArrayInputStream(script.getBytes(UTF_8)),
                new PrintStream(printed, true, UTF_8)) {
            @Override
            public void pause(long millis) {
            }
        };
    }

    private String screen(ByteArrayOutputStream printed) {
        return printed.toString(UTF_8).replace("\r\n", "\n");
    }
}
