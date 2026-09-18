import org.junit.jupiter.api.Test;
import menu.*;

import static org.junit.jupiter.api.Assertions.*;

class MenuValidationTest {

    @Test
    void carTypeMenuRange() {

        Menu menu = new CarTypeMenu();

        assertTrue(menu.isValid(1));
        assertTrue(menu.isValid(2));
        assertTrue(menu.isValid(3));

        assertFalse(menu.isValid(0));
        assertFalse(menu.isValid(4));
    }

    @Test
    void engineMenuRange() {

        Menu menu = new EngineMenu();

        assertTrue(menu.isValid(0));
        assertTrue(menu.isValid(1));
        assertTrue(menu.isValid(4));

        assertFalse(menu.isValid(-1));
        assertFalse(menu.isValid(5));
    }

    @Test
    void brakeMenuRange() {

        Menu menu = new BrakeSystemMenu();

        assertTrue(menu.isValid(0));
        assertTrue(menu.isValid(1));
        assertTrue(menu.isValid(3));

        assertFalse(menu.isValid(-1));
        assertFalse(menu.isValid(4));
    }

    @Test
    void steeringMenuRange() {

        Menu menu = new SteeringSystemMenu();

        assertTrue(menu.isValid(0));
        assertTrue(menu.isValid(1));
        assertTrue(menu.isValid(2));

        assertFalse(menu.isValid(-1));
        assertFalse(menu.isValid(3));
    }

    @Test
    void runTestMenuRange() {

        Menu menu = new RunTestMenu();

        assertTrue(menu.isValid(0));
        assertTrue(menu.isValid(1));
        assertTrue(menu.isValid(2));

        assertFalse(menu.isValid(-1));
        assertFalse(menu.isValid(3));
    }
}