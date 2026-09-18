package parts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelectablePartTest {
    @Test
    @DisplayName("모든 부품은 코드와 표시 이름을 제공한다")
    void exposesPartInformation() {
        assertThat(CarType.SEDAN.getCode()).isEqualTo(1);
        assertThat(CarType.SEDAN.getDisplayName()).isEqualTo("Sedan");
        assertThat(Engine.BROKEN.getDisplayName()).isEqualTo("고장난 엔진");
        assertThat(BrakeSystem.CONTINENTAL.getDisplayName()).isEqualTo("Continental");
        assertThat(SteeringSystem.MOBIS.getDisplayName()).isEqualTo("Mobis");
    }

    @Test
    @DisplayName("각 부품은 유효한 코드를 판별하고 해당 부품으로 변환한다")
    void validatesAndConvertsCodes() {
        assertThat(CarType.isValidCode(3)).isTrue();
        assertThat(CarType.fromCode(3)).isEqualTo(CarType.TRUCK);
        assertThat(Engine.isValidCode(4)).isTrue();
        assertThat(Engine.fromCode(4)).isEqualTo(Engine.BROKEN);
        assertThat(BrakeSystem.isValidCode(2)).isTrue();
        assertThat(BrakeSystem.fromCode(2)).isEqualTo(BrakeSystem.CONTINENTAL);
        assertThat(SteeringSystem.isValidCode(1)).isTrue();
        assertThat(SteeringSystem.fromCode(1)).isEqualTo(SteeringSystem.BOSCH);
    }

    @Test
    @DisplayName("지원하지 않는 코드는 거부하고 부품별 오류를 알린다")
    void rejectsUnsupportedCodes() {
        assertThat(CarType.isValidCode(0)).isFalse();
        assertThat(Engine.isValidCode(5)).isFalse();
        assertThat(BrakeSystem.isValidCode(-1)).isFalse();
        assertThat(SteeringSystem.isValidCode(3)).isFalse();

        assertThatThrownBy(() -> CarType.fromCode(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 차량 타입 코드입니다: 0");
        assertThatThrownBy(() -> Engine.fromCode(5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 엔진 코드입니다: 5");
        assertThatThrownBy(() -> BrakeSystem.fromCode(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 제동장치 코드입니다: 0");
        assertThatThrownBy(() -> SteeringSystem.fromCode(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 조향장치 코드입니다: 3");
    }

    @Test
    @DisplayName("선택 가능한 코드 범위를 계산한다")
    void calculatesCodeRange() {
        assertThat(SelectablePart.codeRange(CarType.values())).isEqualTo("1 ~ 3");
        assertThat(SelectablePart.codeRange(Engine.values())).isEqualTo("1 ~ 4");
        assertThat(SelectablePart.codeRange(SteeringSystem.values())).isEqualTo("1 ~ 2");
    }

    @Test
    @DisplayName("고장 엔진만 고장 상태를 가진다")
    void identifiesBrokenEngine() {
        assertThat(Engine.GM.isBroken()).isFalse();
        assertThat(Engine.TOYOTA.isBroken()).isFalse();
        assertThat(Engine.WIA.isBroken()).isFalse();
        assertThat(Engine.BROKEN.isBroken()).isTrue();
    }
}
