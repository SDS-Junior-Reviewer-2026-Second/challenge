import menu.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import state.Step;

import static org.junit.jupiter.api.Assertions.*;

class MenuFactoryTest {

    private MenuFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MenuFactory();
    }

    @Test
    void createCarTypeMenu() {

        Menu menu =
                factory.create(Step.CAR_TYPE);

        assertInstanceOf(
                CarTypeMenu.class,
                menu
        );
    }

    @Test
    void createEngineMenu() {

        Menu menu =
                factory.create(Step.ENGINE);

        assertInstanceOf(
                EngineMenu.class,
                menu
        );
    }

    @Test
    void createBrakeMenu() {

        Menu menu =
                factory.create(Step.BRAKE_SYSTEM);

        assertInstanceOf(
                BrakeSystemMenu.class,
                menu
        );
    }

    @Test
    void createSteeringMenu() {

        Menu menu =
                factory.create(Step.STEERING_SYSTEM);

        assertInstanceOf(
                SteeringSystemMenu.class,
                menu
        );
    }

    @Test
    void createRunTestMenu() {

        Menu menu =
                factory.create(Step.RUN_TEST);

        assertInstanceOf(
                RunTestMenu.class,
                menu
        );
    }
}