import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EngineTest {

    @Test
    void fromCode_knownCodes_returnMatchingConstant() {
        assertThat(Engine.fromCode(1)).isEqualTo(Engine.GM);
        assertThat(Engine.fromCode(2)).isEqualTo(Engine.TOYOTA);
        assertThat(Engine.fromCode(3)).isEqualTo(Engine.WIA);
        assertThat(Engine.fromCode(4)).isEqualTo(Engine.BROKEN);
    }

    @Test
    void fromCode_unknownCode_throws() {
        assertThatThrownBy(() -> Engine.fromCode(9)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void maxCode_matchesNumberOfConstants() {
        assertThat(Engine.maxCode()).isEqualTo(4);
    }

    @Test
    void isBroken_onlyTrueForBrokenConstant() {
        assertThat(Engine.BROKEN.isBroken()).isTrue();
        assertThat(Engine.GM.isBroken()).isFalse();
        assertThat(Engine.TOYOTA.isBroken()).isFalse();
        assertThat(Engine.WIA.isBroken()).isFalse();
    }

    @Test
    void displayName_matchesExpectedText() {
        assertThat(Engine.GM.getDisplayName()).isEqualTo("GM");
        assertThat(Engine.TOYOTA.getDisplayName()).isEqualTo("TOYOTA");
        assertThat(Engine.WIA.getDisplayName()).isEqualTo("WIA");
        assertThat(Engine.BROKEN.getDisplayName()).isEqualTo("고장난 엔진");
    }
}
