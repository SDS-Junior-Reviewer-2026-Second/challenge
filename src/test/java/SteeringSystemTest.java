import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SteeringSystemTest {

    @Test
    void fromCode_knownCodes_returnMatchingConstant() {
        assertThat(SteeringSystem.fromCode(1)).isEqualTo(SteeringSystem.BOSCH);
        assertThat(SteeringSystem.fromCode(2)).isEqualTo(SteeringSystem.MOBIS);
    }

    @Test
    void fromCode_unknownCode_throws() {
        assertThatThrownBy(() -> SteeringSystem.fromCode(9)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void maxCode_matchesNumberOfConstants() {
        assertThat(SteeringSystem.maxCode()).isEqualTo(2);
    }

    @Test
    void menuName_isUpperCase() {
        assertThat(SteeringSystem.BOSCH.getMenuName()).isEqualTo("BOSCH");
        assertThat(SteeringSystem.MOBIS.getMenuName()).isEqualTo("MOBIS");
    }

    @Test
    void reportName_isTitleCase() {
        assertThat(SteeringSystem.BOSCH.getReportName()).isEqualTo("Bosch");
        assertThat(SteeringSystem.MOBIS.getReportName()).isEqualTo("Mobis");
    }
}
