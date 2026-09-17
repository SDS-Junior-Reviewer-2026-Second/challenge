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

    @Test
    void endOfInputStopsTheLoopWithoutError() {
        FakeConsole console = new FakeConsole("1", "1");

        new AssembleApp(console).run();

        assertThat(console.output())
                .contains("GM 엔진을 선택하셨습니다.")
                .contains("어떤 제동장치를 선택할까요?")
                .doesNotContain("바이바이");
    }
}
