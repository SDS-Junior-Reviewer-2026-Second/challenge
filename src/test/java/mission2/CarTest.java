package mission2;

import mission2.car.Car;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarTest {

    Car car = new Car();
    Car spyCar = spy(car);

    @Test
    void executeStep_정상_input() {
        // given
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(1);

        // when
        spyCar.executeStep(2);

        // then
        verify(spyCar,times(0)).fail(anyString());
    }

    @Test
    void executeStep_정상2_input() {
        // given
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(1);

        // when
        spyCar.executeStep(1);

        // then
        verify(spyCar).runProducedCar();
    }

    @Test
    void executeStep_이전단계_요청_input() {
        // given
        car.executeStep(2);

        // when
        car.executeStep(0);

        // then
        assertEquals(0,car.getStep());
    }

    @Test
    void executeStep_처음부터_다시하기_input() {
        //given
        car.executeStep(2);
        car.executeStep(2);
        car.executeStep(2);
        car.executeStep(2);

        // when
        car.executeStep(0);

        //then
        assertEquals(0,car.getStep());
    }

    @Test
    void executeStep_잘못만든_자동차_input() {
        //given
        spyCar.executeStep(2);
        spyCar.executeStep(2);
        spyCar.executeStep(2);
        spyCar.executeStep(2);

        // when
        spyCar.executeStep(2);

        //then
        verify(spyCar).fail(anyString());
    }

    @Test
    void executeStep_고장난_엔진의_자동차_input() {
        //given
        spyCar.executeStep(1);
        spyCar.executeStep(4);
        spyCar.executeStep(1);
        spyCar.executeStep(1);

        //when
        spyCar.executeStep(1);
        spyCar.executeStep(2);

        //then
        verify(spyCar,times(0)).fail(anyString());
    }

    @Test
    void executeStep_잘못만든_자동차_input2() {
        //given
        spyCar.executeStep(2);
        spyCar.executeStep(2);
        spyCar.executeStep(2);
        spyCar.executeStep(2);

        // then
        assertFalse(spyCar.isValidCheck());
    }


    @Test
    void executeStep_범위밖_input_1() {

        // when
        car.executeStep(0);

        // then
        assertEquals(0,car.getStep());
    }

    @Test
    void executeStep_범위밖_input_2() {
        //given
        car.executeStep(2);
        car.executeStep(2);
        car.executeStep(2);
        car.executeStep(2);

        //when, then
        assertFalse(car.isValidRange(4));
        assertFalse(car.isValidRange(-1));
    }

    @Test
    void executeStep_logic_테스트_1() {
        // given
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(3);
        spyCar.executeStep(2);

        // when
        spyCar.executeStep(2);

        // then
        assertFalse(spyCar.isValidCheck());
        verify(spyCar).fail(anyString());
    }

    @Test
    void executeStep_logic_테스트_2() {
        // given
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(2);
        spyCar.executeStep(1);

        //when
        spyCar.executeStep(2);

        //then
        assertFalse(spyCar.isValidCheck());
        verify(spyCar).fail(anyString());
    }
    @Test
    void executeStep_logic_테스트_3() {
        //given
        spyCar.executeStep(2);
        spyCar.executeStep(2);
        spyCar.executeStep(1);
        spyCar.executeStep(1);

        //when
        spyCar.executeStep(2);

        //then
        assertFalse(spyCar.isValidCheck());
        verify(spyCar).fail(anyString());
    }
    @Test
    void executeStep_logic_테스트_4() {
        //given
        spyCar.executeStep(3);
        spyCar.executeStep(3);
        spyCar.executeStep(1);
        spyCar.executeStep(1);

        //when
        spyCar.executeStep(2);

        //then
        assertFalse(spyCar.isValidCheck());
        verify(spyCar).fail(anyString());
    }
    @Test
    void executeStep_logic_테스트_5() {
        //given
        spyCar.executeStep(3);
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(1);

        //when
        spyCar.executeStep(1);
        spyCar.executeStep(2);

        //then
        assertFalse(spyCar.isValidCheck());
        verify(spyCar).fail(anyString());
    }

    @Test
    void executeStep_coverage_테스트_1() {
        //given
        spyCar.executeStep(2);
        spyCar.executeStep(1);
        spyCar.executeStep(1);
        spyCar.executeStep(1);

        //when
        spyCar.executeStep(1);
        spyCar.executeStep(2);

        //then
        assertTrue(spyCar.isValidCheck());
        verify(spyCar,times(0)).fail(anyString());
    }

    @Test
    void executeStep_coverage_테스트_2() {
        //given
        spyCar.executeStep(3);
        spyCar.executeStep(1);
        spyCar.executeStep(3);
        spyCar.executeStep(1);

        //when
        spyCar.executeStep(1);
        spyCar.executeStep(2);

        //then
        assertTrue(spyCar.isValidCheck());
        verify(spyCar,times(0)).fail(anyString());
    }


    @Test
    void executeStep_coverage_테스트_3() {
        //given
        spyCar.executeStep(3);
        spyCar.executeStep(1);
        spyCar.executeStep(3);
        spyCar.executeStep(2);

        //when
        spyCar.executeStep(1);
        spyCar.executeStep(2);

        //then
        assertFalse(spyCar.isValidCheck());
        verify(spyCar).fail(anyString());
    }

}