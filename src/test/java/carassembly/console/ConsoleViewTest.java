package carassembly.console;

import carassembly.application.AssemblyStep;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleViewTest {
    @Test
    void 메뉴_순서와_선택_이름을_유지한다() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner input = new Scanner("");
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            ConsoleView view = new ConsoleView(input, output);
            view.showMenu(AssemblyStep.CAR_TYPE);
            view.showMenu(AssemblyStep.ENGINE);
            view.showMenu(AssemblyStep.BRAKE_SYSTEM);
            view.showMenu(AssemblyStep.STEERING_SYSTEM);
            view.showMenu(AssemblyStep.RUN_TEST);
        }

        String output = bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertTrue(output.contains("1. Sedan\n2. SUV\n3. Truck\n"));
        assertTrue(output.contains("1. GM\n2. TOYOTA\n3. WIA\n4. 고장난 엔진\n"));
        assertTrue(output.contains("1. MANDO\n2. CONTINENTAL\n3. BOSCH\n"));
        assertTrue(output.contains("1. BOSCH\n2. MOBIS\n"));
        assertTrue(output.contains("0. 처음 화면으로 돌아가기\n1. RUN\n2. Test\n"));
    }

    @Test
    void 빈_입력과_입력_종료를_구별한다() {
        try (Scanner input = new Scanner(" \n");
             PrintStream output = new PrintStream(new ByteArrayOutputStream())) {
            ConsoleView view = new ConsoleView(input, output);

            assertEquals(Optional.of(""), view.readCommand());
            assertEquals(Optional.empty(), view.readCommand());
        }
    }
}
