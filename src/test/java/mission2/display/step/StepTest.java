package mission2.display.step;

import mission2.car.AssemblyOrder;
import mission2.car.CarAction;
import mission2.car.part.Engines;
import mission2.car.part.Part;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("조립 단계")
class StepTest {

    private final List<Step> line = StepFactory.assemblyLine();
    private final Recorder recorder = new Recorder();

    @Test
    @DisplayName("부품 분류마다 단계가 하나씩 생기고 마지막에 동작 단계가 붙는다")
    void buildsOneStepPerCategoryPlusAction() {
        assertThat(line).hasSize(AssemblyOrder.categories().size() + 1);
        assertThat(line.get(0)).isInstanceOf(PartStep.class);
        assertThat(line.get(line.size() - 1)).isInstanceOf(ActionStep.class);
    }

    @ParameterizedTest(name = "첫 단계 {0}번 -> {1}")
    @CsvSource({"-1, false", "0, false", "1, true", "3, true", "4, false"})
    @DisplayName("첫 단계는 뒤로가기가 없어 1번부터만 받는다")
    void firstStepRejectsBack(int code, boolean accepted) {
        assertThat(line.get(0).accepts(code)).isEqualTo(accepted);
    }

    @ParameterizedTest(name = "엔진 단계 {0}번 -> {1}")
    @CsvSource({"-1, false", "0, true", "1, true", "4, true", "5, false"})
    @DisplayName("뒤로가기가 있는 단계는 0번도 받는다")
    void partStepAcceptsBack(int code, boolean accepted) {
        assertThat(line.get(1).accepts(code)).isEqualTo(accepted);
    }

    @Test
    @DisplayName("부품 단계는 고른 번호의 부품을 넘겨준다")
    void partStepHandsOverThePart() {
        line.get(1).select(4, recorder);

        assertThat(recorder.part).isEqualTo(Engines.BROKEN);
        assertThat(recorder.action).isNull();
    }

    @Test
    @DisplayName("동작 단계는 고른 번호의 동작을 넘겨준다")
    void actionStepHandsOverTheAction() {
        line.get(line.size() - 1).select(2, recorder);

        assertThat(recorder.action).isEqualTo(CarAction.TEST);
        assertThat(recorder.part).isNull();
    }

    @Test
    @DisplayName("첫 화면은 그림과 구분선으로 시작한다")
    void opensWithTheCarPicture() {
        assertThat(line.get(0).lines())
                .containsSubsequence("        ______________", Step.SEPARATOR, "어떤 차량 타입을 선택할까요?")
                .doesNotContain("0. 뒤로가기");
    }

    @Test
    @DisplayName("화면은 질문, 뒤로가기, 항목, 구분선 순으로 그린다")
    void drawsLinesInOrder() {
        assertThat(line.get(1).lines()).containsExactly(
                "어떤 엔진을 탑재할까요?",
                "0. 뒤로가기",
                "1. GM",
                "2. TOYOTA",
                "3. WIA",
                "4. 고장난 엔진",
                Step.SEPARATOR);
    }

    @Test
    @DisplayName("부품 단계의 범위 안내는 분류 이름과 항목 수로 만든다")
    void buildsPartStepRangeError() {
        assertThat(line.get(1).rangeError()).isEqualTo("엔진은 1 ~ 4 범위만 선택 가능");
        assertThat(line.get(0).rangeError()).isEqualTo("차량 타입은 1 ~ 3 범위만 선택 가능");
    }

    @Test
    @DisplayName("동작 단계의 범위 안내는 동작 이름으로 말한다")
    void buildsActionStepRangeError() {
        assertThat(line.get(line.size() - 1).rangeError()).isEqualTo("Run 또는 Test 중 하나를 선택 필요");
    }

    private static class Recorder implements StepListener {

        private Part part;
        private CarAction action;

        @Override
        public void partSelected(Part part) {
            this.part = part;
        }

        @Override
        public void actionSelected(CarAction action) {
            this.action = action;
        }
    }
}
