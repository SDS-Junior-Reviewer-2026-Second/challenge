package mission2;

import mission2.display.Display;
import mission2.display.step.StepFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("조립 흐름")
class AssembleTest {

    @Test
    @DisplayName("부품을 다 고르면 조합 검사가 PASS 한다")
    void testPasses() {
        assertThat(play("1", "1", "1", "2", "2")).contains("자동차 부품 조합 테스트 결과 : PASS");
    }

    @Test
    @DisplayName("쓸 수 없는 조합은 FAIL 과 사유를 보여 준다")
    void testFailsWithReason() {
        assertThat(play("1", "1", "2", "2", "2"))
                .contains("자동차 부품 조합 테스트 결과 : FAIL")
                .contains("Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    @DisplayName("정상 조합으로 RUN 하면 사양과 함께 주행한다")
    void runsWithSpec() {
        assertThat(play("1", "1", "1", "2", "1"))
                .contains("Car Type : Sedan")
                .contains("Brake    : Mando")
                .contains("자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("쓸 수 없는 조합으로 RUN 하면 동작하지 않는다")
    void doesNotRunWithBadCombination() {
        assertThat(play("1", "1", "2", "2", "1")).contains("자동차가 동작되지 않습니다");
    }

    @Test
    @DisplayName("고장난 엔진으로 RUN 하면 움직이지 않는다")
    void doesNotRunWithBrokenEngine() {
        assertThat(play("1", "4", "1", "2", "1")).contains("엔진이 고장나있습니다.");
    }

    @Test
    @DisplayName("부품을 고르면 고른 것을 확인해 준다")
    void confirmsEachSelection() {
        assertThat(play("1", "1", "1", "2"))
                .contains("차량 타입으로 Sedan을 선택하셨습니다.")
                .contains("GM 엔진을 선택하셨습니다.")
                .contains("MANDO 제동장치를 선택하셨습니다.")
                .contains("MOBIS 조향장치를 선택하셨습니다.");
    }

    @Test
    @DisplayName("숫자가 아니거나 범위를 벗어난 입력은 안내만 한다")
    void rejectsInvalidInput() {
        assertThat(play("abc", "9", "-1"))
                .contains("ERROR :: 숫자만 입력 가능")
                .contains("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
    }

    @Test
    @DisplayName("0번은 이전 단계로 돌아간다")
    void goesBackToPreviousStep() {
        FakeDisplay display = playWith("1", "0");

        assertThat(display.countOf("어떤 차량 타입을 선택할까요?")).isEqualTo(2);
    }

    @Test
    @DisplayName("마지막 단계에서 0번을 누르면 처음부터 다시 조립한다")
    void restartsFromTheFirstStep() {
        FakeDisplay display = playWith("1", "1", "1", "2", "0", "2", "1", "1", "2", "1");

        assertThat(display.countOf("어떤 차량 타입을 선택할까요?")).isEqualTo(2);
        assertThat(display.screen()).contains("Car Type : SUV");
    }

    @Test
    @DisplayName("첫 화면에는 뒤로가기가 없다")
    void firstStepHasNoBack() {
        assertThat(play("0"))
                .doesNotContain("0. 뒤로가기")
                .contains("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
    }

    @Test
    @DisplayName("exit 를 넣으면 인사하고 끝난다")
    void exitsOnCommand() {
        assertThat(play("exit")).endsWith("바이바이");
    }

    private String play(String... inputs) {
        return playWith(inputs).screen();
    }

    private FakeDisplay playWith(String... inputs) {
        FakeDisplay display = new FakeDisplay(inputs);
        new Assemble(display, StepFactory.assemblyLine()).run();
        return display;
    }

    private static class FakeDisplay implements Display {

        private final Queue<String> inputs;
        private final List<String> printed = new ArrayList<>();

        FakeDisplay(String... inputs) {
            this.inputs = new ArrayDeque<>(List.of(inputs));
        }

        @Override
        public void redraw(List<String> screen) {
            show(screen);
        }

        @Override
        public void show(List<String> lines) {
            printed.addAll(lines);
        }

        @Override
        public String ask() {
            return inputs.poll();
        }

        @Override
        public void pause(long millis) {
        }

        String screen() {
            return String.join("\n", printed);
        }

        long countOf(String line) {
            return printed.stream().filter(line::equals).count();
        }
    }
}
