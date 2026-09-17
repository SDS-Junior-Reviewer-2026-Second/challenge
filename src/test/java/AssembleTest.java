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
    @DisplayName("숫자가 아니거나 범위를 벗어난 입력은 안내만 한다")
    void rejectsInvalidInput() {
        assertThat(play("abc", "9"))
                .contains("ERROR :: 숫자만 입력 가능")
                .contains("ERROR :: [차량 타입] 1 ~ 3 사이의 번호만 선택 가능");
    }

    @Test
    @DisplayName("0번은 이전 단계로 돌아간다")
    void goesBackToPreviousStep() {
        FakeDisplay display = playWith("1", "0");

        assertThat(display.countOf("어떤 차량 타입을 선택할까요?")).isEqualTo(2);
    }

    @Test
    @DisplayName("첫 화면에는 뒤로가기가 없다")
    void firstStepHasNoBack() {
        assertThat(play("0"))
                .doesNotContain("0. 뒤로가기")
                .contains("ERROR :: [차량 타입] 1 ~ 3 사이의 번호만 선택 가능");
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
        new Assemble(display, AssemblyFactory.createSteps()).run();
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
