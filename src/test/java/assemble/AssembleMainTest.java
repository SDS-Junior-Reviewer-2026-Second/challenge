package assemble;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/** main 이 SystemConsole 과 올바르게 연결되는지만 확인한다. delay 를 타지 않는 시나리오만 쓴다. */
class AssembleMainTest {

    private InputStream originalIn;
    private PrintStream originalOut;

    @BeforeEach
    void saveStreams() {
        originalIn = System.in;
        originalOut = System.out;
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void mainReadsStdinAndWritesStdout() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream("exit\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));

        Assemble.main(new String[0]);

        assertThat(captured.toString(StandardCharsets.UTF_8))
                .contains("어떤 차량 타입을 선택할까요?")
                .contains("바이바이");
    }
}
