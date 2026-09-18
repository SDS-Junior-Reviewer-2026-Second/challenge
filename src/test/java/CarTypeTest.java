import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTypeTest {

    @Test
    void fromCode_knownCodes_returnMatchingConstant() {
        assertThat(CarType.fromCode(1)).isEqualTo(CarType.SEDAN);
        assertThat(CarType.fromCode(2)).isEqualTo(CarType.SUV);
        assertThat(CarType.fromCode(3)).isEqualTo(CarType.TRUCK);
    }

    @Test
    void fromCode_unknownCode_throws() {
        assertThatThrownBy(() -> CarType.fromCode(9)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void maxCode_matchesNumberOfConstants() {
        assertThat(CarType.maxCode()).isEqualTo(3);
    }

    @Test
    void displayName_matchesExpectedText() {
        assertThat(CarType.SEDAN.getDisplayName()).isEqualTo("Sedan");
        assertThat(CarType.SUV.getDisplayName()).isEqualTo("SUV");
        assertThat(CarType.TRUCK.getDisplayName()).isEqualTo("Truck");
    }
}
