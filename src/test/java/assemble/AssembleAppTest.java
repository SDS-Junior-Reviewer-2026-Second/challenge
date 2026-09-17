package assemble;

import assemble.io.FakeConsole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssembleAppTest {

    @Test
    void fullRunPrintsSpec() {
        FakeConsole console = new FakeConsole("1", "1", "1", "1", "1", "exit");

        new AssembleApp(console).run();

        assertThat(console.output())
                .contains("Car Type : Sedan")
                .contains("Engine   : GM")
                .contains("Brake    : Mando")
                .contains("Steering : Bosch")
                .contains("자동차가 동작됩니다.")
                .contains("바이바이");
    }
}
