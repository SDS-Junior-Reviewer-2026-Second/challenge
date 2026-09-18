package mission2.car;

import mission2.car.part.Brakes;
import mission2.car.part.CarTypes;
import mission2.car.part.Engines;
import mission2.car.part.SteeringSystems;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("조립 순서")
class AssemblyOrderTest {

    @Test
    @DisplayName("등록한 분류를 순서대로 돌려준다")
    void listsCategoriesInOrder() {
        assertThat(AssemblyOrder.categories()).containsExactly(
                CarTypes.CATEGORY,
                Engines.CATEGORY,
                Brakes.CATEGORY,
                SteeringSystems.CATEGORY);
    }
}
