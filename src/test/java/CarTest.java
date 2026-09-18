import car.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void selectComponents() {

        Car car = new Car();

        car.selectCarType(CarType.SEDAN);
        car.selectEngine(Engine.GM);
        car.selectBrakeSystem(BrakeSystem.MANDO);
        car.selectSteeringSystem(SteeringSystem.BOSCH);

        assertAll(
                () -> assertEquals(CarType.SEDAN, car.getType()),
                () -> assertEquals(Engine.GM, car.getEngine()),
                () -> assertEquals(
                        BrakeSystem.MANDO,
                        car.getBrakeSystem()
                ),
                () -> assertEquals(
                        SteeringSystem.BOSCH,
                        car.getSteeringSystem()
                )
        );
    }
}