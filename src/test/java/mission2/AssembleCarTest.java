package mission2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AssembleCarTest {
    private final InputStream inputStream = System.in;
    private final PrintStream outputStream = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(inputStream);
        System.setOut(outputStream);
    }

    @Test
    @DisplayName("BrakeSystemMenu_잘못된범위_프로그램종료여부")
    void BrakeSystemMenu_잘못된범위_프로그램종료여부() throws InterruptedException {
        String input = "1\n1\n5\nexit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[0]);

        String out = outContent.toString();
        assertTrue(out.contains("ERROR :: 제동장치는"));
    }


    @Test
    @DisplayName("정상입력시_마지막_RunTest진입확인")
    void 정상입력시_마지막_RunTest진입확인() throws InterruptedException {
        String input = "1\n1\n1\n1\n2\nexit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[0]);

        String out = outContent.toString();
        assertTrue(out.contains("멋진 차량이 완성되었습니다."));
    }

    @Test
    @DisplayName("정상입력시_마지막_RunTest확인2")
    void 정상입력시_마지막_RunTest확인2() throws InterruptedException {
        String input = "1\n1\n1\n1\n1\nexit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[0]);

        String out = outContent.toString();
        assertTrue(out.contains("멋진 차량이 완성되었습니다."));
    }

    @Test
    @DisplayName("Exit을_누르면_바로_나간다")
    void Exit을_누르면_바로_나간다() throws InterruptedException {
        String input = "exit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[0]);

        String out = outContent.toString();
        assertTrue(out.contains("바이바이"));
    }


    @Test
    @DisplayName("EngineMenu_잘못된범위_입력시_프로그램종료여부")
    void EngineMenu_잘못된범위_입력시_프로그램종료여부() throws InterruptedException {
        String input = "1\n5\nexit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[0]);

        String out = outContent.toString();
        assertTrue(out.contains("ERROR :: 엔진은 "));
    }

    @Test
    @DisplayName("SteeringMenu_잘못된범위_입력처리여부")
    void SteeringMenu_잘못된범위_입력처리여부() throws InterruptedException {
        String input = "1\n1\n1\n5\nexit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[0]);

        String out = outContent.toString();
        assertTrue(out.contains("ERROR :: 조향장치는"));
    }


    @Test
    void 잘못된형식_입력_예외처리() throws InterruptedException {
        String input = "asdf\nexit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[1]);

        String out = outContent.toString();
        assertTrue(out.contains("ERROR :: 숫자만 입력 가능"));

    }

    @Test
    void 잘못된범위_입력_예외처리() throws InterruptedException {
        String input = "10\nexit";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        AssembleCar.main(new String[1]);

        String out = outContent.toString();
        assertTrue(out.contains("ERROR :: 차량 타입은"));
    }
}
