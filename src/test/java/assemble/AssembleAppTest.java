package assemble;

import assemble.io.FakeConsole;
import assemble.rule.CarInspector;
import assemble.rule.CompatibilityRules;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssembleAppTest {

    private static final CarInspector STANDARD_INSPECTOR = new CarInspector(CompatibilityRules.ALL);

    @Test
    void fullRunPrintsSpec() {
        FakeConsole console = new FakeConsole("1", "1", "1", "1", "1", "exit");

        new AssembleApp(console, STANDARD_INSPECTOR).run();

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

        new AssembleApp(console, STANDARD_INSPECTOR).run();

        assertThat(console.output())
                .contains("GM 엔진을 선택하셨습니다.")
                .contains("어떤 제동장치를 선택할까요?")
                .doesNotContain("바이바이");
    }

    @Test
    void injectedRulesDecideWhetherTheCarRuns() {
        CarInspector noRules = new CarInspector(List.of());
        // Sedan + Continental 은 표준 규칙에서는 위반이지만, 규칙이 없으면 동작해야 한다.
        FakeConsole console = new FakeConsole("1", "1", "2", "1", "1", "exit");

        new AssembleApp(console, noRules).run();

        assertThat(console.output())
                .contains("Brake    : Continental")
                .contains("자동차가 동작됩니다.")
                .doesNotContain("자동차가 동작되지 않습니다");
    }
}
