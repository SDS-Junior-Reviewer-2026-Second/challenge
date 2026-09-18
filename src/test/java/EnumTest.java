import car.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumTest {

    @Test
    @DisplayName("차량 타입 번호를 enum으로 변환")
    void carTypeFromNumber() {

        assertEquals(
                CarType.SEDAN,
                CarType.from(1)
        );

        assertEquals(
                CarType.SUV,
                CarType.from(2)
        );

        assertEquals(
                CarType.TRUCK,
                CarType.from(3)
        );
    }

    @Test
    @DisplayName("엔진 번호를 enum으로 변환")
    void engineFromNumber() {

        assertEquals(Engine.GM, Engine.from(1));
        assertEquals(Engine.TOYOTA, Engine.from(2));
        assertEquals(Engine.WIA, Engine.from(3));
        assertEquals(Engine.BROKEN, Engine.from(4));
    }

    @Test
    @DisplayName("잘못된 차량 번호면 예외 발생")
    void invalidCarTypeNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> CarType.from(4)
        );
    }

    @Test
    @DisplayName("잘못된 엔진 번호면 예외 발생")
    void invalidEngineNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Engine.from(5)
        );
    }

    @Test
    @DisplayName("BROKEN 엔진은 고장 상태이다")
    void brokenEngine() {

        assertTrue(Engine.BROKEN.isBroken());
        assertFalse(Engine.GM.isBroken());
    }
}