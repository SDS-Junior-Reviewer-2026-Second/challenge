import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BrakeSystemTest {

    @Test
    void fromCode_knownCodes_returnMatchingConstant() {
        assertThat(BrakeSystem.fromCode(1)).isEqualTo(BrakeSystem.MANDO);
        assertThat(BrakeSystem.fromCode(2)).isEqualTo(BrakeSystem.CONTINENTAL);
        assertThat(BrakeSystem.fromCode(3)).isEqualTo(BrakeSystem.BOSCH);
    }

    @Test
    void fromCode_unknownCode_throws() {
        assertThatThrownBy(() -> BrakeSystem.fromCode(9)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void maxCode_matchesNumberOfConstants() {
        assertThat(BrakeSystem.maxCode()).isEqualTo(3);
    }

    @Test
    void menuName_isUpperCase() {
        assertThat(BrakeSystem.MANDO.getMenuName()).isEqualTo("MANDO");
        assertThat(BrakeSystem.CONTINENTAL.getMenuName()).isEqualTo("CONTINENTAL");
        assertThat(BrakeSystem.BOSCH.getMenuName()).isEqualTo("BOSCH");
    }

    @Test
    void reportName_isTitleCase() {
        assertThat(BrakeSystem.MANDO.getReportName()).isEqualTo("Mando");
        assertThat(BrakeSystem.CONTINENTAL.getReportName()).isEqualTo("Continental");
        assertThat(BrakeSystem.BOSCH.getReportName()).isEqualTo("Bosch");
    }
}
