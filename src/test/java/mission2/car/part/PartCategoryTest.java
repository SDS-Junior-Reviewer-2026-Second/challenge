package mission2.car.part;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("부품 분류")
class PartCategoryTest {

    @Test
    @DisplayName("분류마다 자기 부품 목록을 순서대로 가진다")
    void keepsItsOwnParts() {
        assertThat(CarTypes.CATEGORY.partLabels()).containsExactly("Sedan", "SUV", "Truck");
        assertThat(Engines.CATEGORY.parts())
                .containsExactly(Engines.GM, Engines.TOYOTA, Engines.WIA, Engines.BROKEN);
    }

    @Test
    @DisplayName("부품은 자기 분류와 고장 여부를 안다")
    void partKnowsItsCategoryAndCondition() {
        assertThat(Engines.BROKEN.category()).isEqualTo(Engines.CATEGORY);
        assertThat(Engines.BROKEN.isBroken()).isTrue();
        assertThat(Engines.GM.isBroken()).isFalse();
    }

    @Test
    @DisplayName("목록에 뜨는 이름과 사양에 적히는 이름은 따로 가질 수 있다")
    void keepsLabelApartFromName() {
        assertThat(Brakes.MANDO.label()).isEqualTo("MANDO");
        assertThat(Brakes.MANDO.name()).isEqualTo("Mando");
        assertThat(Engines.GM.label()).isEqualTo(Engines.GM.name());
    }

    @Test
    @DisplayName("분류가 자기 선택 문구를 만든다")
    void buildsItsOwnSelectionMessage() {
        assertThat(CarTypes.SEDAN.selectionMessage()).isEqualTo("차량 타입으로 Sedan을 선택하셨습니다.");
        assertThat(Brakes.MANDO.selectionMessage()).isEqualTo("MANDO 제동장치를 선택하셨습니다.");
    }

    @Test
    @DisplayName("분류는 자기 이름과 화면 문구를 안다")
    void knowsItsTitleAndTexts() {
        assertThat(Brakes.CATEGORY.title()).isEqualTo("제동장치");
        assertThat(Brakes.CATEGORY.question()).isEqualTo("어떤 제동장치를 선택할까요?");
        assertThat(Brakes.CATEGORY.specLabel()).isEqualTo("Brake");
    }
}
