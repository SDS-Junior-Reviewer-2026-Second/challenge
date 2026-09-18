package mission2;

import mission2.car.BrakeSystem;
import mission2.car.CarType;
import mission2.car.Engine;
import mission2.car.SteeringSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectOptionInterfaceTest {

    BrakeSystem brakeSystem = new BrakeSystem();
    CarType carType = new CarType();
    Engine engine = new Engine();
    SteeringSystem steeringSystem = new SteeringSystem();

    @Test
    void brake_validCheck() {
        //then
        assertFalse(brakeSystem.validCheck(-1));
        assertTrue(brakeSystem.validCheck(0));
        assertTrue(brakeSystem.validCheck(1));
        assertTrue(brakeSystem.validCheck(2));
        assertTrue(brakeSystem.validCheck(3));
        assertFalse(brakeSystem.validCheck(4));
        assertFalse(brakeSystem.validCheck(5));
    }
    @Test
    void carType_validCheck() {
        //then
        assertFalse(carType.validCheck(-1));
        assertFalse(carType.validCheck(0));
        assertTrue(carType.validCheck(1));
        assertTrue(carType.validCheck(2));
        assertTrue(carType.validCheck(3));
        assertFalse(carType.validCheck(4));
        assertFalse(carType.validCheck(5));
    }
    @Test
    void engine_validCheck() {
        //then
        assertFalse(engine.validCheck(-1));
        assertTrue(engine.validCheck(0));
        assertTrue(engine.validCheck(1));
        assertTrue(engine.validCheck(2));
        assertTrue(engine.validCheck(3));
        assertTrue(engine.validCheck(4));
        assertFalse(engine.validCheck(5));
    }
    @Test
    void steeringSystem_validCheck() {
        //then
        assertFalse(steeringSystem.validCheck(-1));
        assertTrue(steeringSystem.validCheck(0));
        assertTrue(steeringSystem.validCheck(1));
        assertTrue(steeringSystem.validCheck(2));
        assertFalse(steeringSystem.validCheck(3));
        assertFalse(steeringSystem.validCheck(4));
        assertFalse(steeringSystem.validCheck(5));
    }

    @Test
    void brake_getName() {
        //given
        brakeSystem.selectOptions(1);
        //then
        assertEquals("Mando", brakeSystem.getBrakeSystemName());

        //given
        brakeSystem.selectOptions(2);
        //then
        assertEquals("Continental", brakeSystem.getBrakeSystemName());

        //given
        brakeSystem.selectOptions(3);
        //then
        assertEquals("Bosch", brakeSystem.getBrakeSystemName());
    }

    @Test
    void steeringSystem_getName() {
        //given
        steeringSystem.selectOptions(1);
        //then
        assertEquals("Bosch", steeringSystem.getSteeringSystemName());

        //given
        steeringSystem.selectOptions(2);
        //then
        assertEquals("Mobis", steeringSystem.getSteeringSystemName());
    }

}