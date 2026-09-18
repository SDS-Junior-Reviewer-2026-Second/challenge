package mission2.menu;

import mission2.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SteeringSystemMenuTest {
    Car car = new Car();
    SteeringSystemMenu sut = new SteeringSystemMenu();

    @BeforeEach
    void make_normal_car(){
        car.setCarType("SUV");
        car.setEngine("GM");
        car.setBrake("Bosch");
        car.setSteering("Bosch");
    }

    @Test
    @DisplayName("유효한인풋은_TRUE반환")
    void 유효한인풋은_TRUE반환(){
        boolean result = sut.isValidInputRange(1);
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("잘못된입력_False반환")
    void 잘못된입력_False반환(){
        boolean result = sut.isValidInputRange(-1);
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("이전으로_돌아가면_BrakeSystemMenu반환")
    void 이전으로_돌아가면_BrakeSystemMenu반환(){
        Menu result = sut.applyAndGetNextMenu(car,0);
        assertThat(result).isInstanceOf(BrakeSystemMenu.class);
    }


}