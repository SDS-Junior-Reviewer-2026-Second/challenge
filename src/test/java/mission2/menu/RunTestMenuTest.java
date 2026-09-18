package mission2.menu;

import mission2.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RunTestMenuTest {
    Car car = new Car();
    RunTestMenu sut = new RunTestMenu();

    @BeforeEach
    void make_normal_car(){
        car.setCarType("SUV");
        car.setEngine("GM");
        car.setBrake("Bosch");
        car.setSteering("Bosch");
    }

    @Test
    @DisplayName("제동장치에_BOSCH제품이면_조향장치도_BOSCH제품_써야한다")
    void 제동장치에_BOSCH제품이면_조향장치도_BOSCH제품_써야한다(){
        car.setBrake("Bosch");
        car.setSteering("WIA");

        String result = RunTestMenu.getValidComponentResult(car);

        assertThat(result).isNotEqualTo("PASS");
    }

    @Test
    @DisplayName("잘못된입력_False반환")
    void 잘못된입력_False반환(){
        boolean result = sut.isValidInputRange(-1);

        assertThat(result).isEqualTo(false);
    }


    @Test
    @DisplayName("Truck에는_Mando제동장치_사용불가")
    void Truck에는_Mando제동장치_사용불가(){
        car.setCarType("Truck");
        car.setBrake("Mando");

        String result = RunTestMenu.getValidComponentResult(car);
        System.out.println(result);
        assertThat(result).isNotEqualTo("PASS");
    }

    @Test
    @DisplayName("Truck에는 WIA엔진 사용 불가")
    void Truck에는_WIA엔진_사용불가(){
        car.setCarType("Truck");
        car.setEngine("WIA");

        String result = RunTestMenu.getValidComponentResult(car);

        assertThat(result).isNotEqualTo("PASS");
    }

    @Test
    @DisplayName("SUV에는 TOYOTA엔진 사용 불가")
    void SUV에는_TOYOTA엔진_사용불가(){
        car.setCarType("SUV");
        car.setEngine("TOYOTA");

        String result = RunTestMenu.getValidComponentResult(car);

        assertThat(result).isNotEqualTo("PASS");
    }

    @Test
    @DisplayName("Sedan에는 Continental제동장치 사용 불가")
    void Sedan에는_Continental제동장치_사용불가(){
        car.setCarType("Sedan");
        car.setBrake("Continental");

        String result = RunTestMenu.getValidComponentResult(car);

        assertThat(result).isNotEqualTo("PASS");
    }

    @Test
    @DisplayName("제한케이스_제외_사용가능")
    void 제한케이스_제외_사용가능(){
        String result = RunTestMenu.getValidComponentResult(car);
        assertThat(result).isEqualTo("PASS");
    }

    @Test
    @DisplayName("인풋_예외처리")
    void 인풋_예외처리(){
        boolean result = sut.isValidInputRange(-3);
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("인풋_정상")
    void 인풋_정상(){
        boolean result = sut.isValidInputRange(1);
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("돌아가기_정상여부")
    void 돌아가기_정상여부() throws InterruptedException {
        Menu result = sut.applyAndGetNextMenu(car,0);
        assertThat(result).isInstanceOf(CarTypeMenu.class);
    }

    @Test
    @DisplayName("runProducedCar_정상여부1")
    void runProducedCar_정상여부1() throws InterruptedException {
        Menu result = sut.applyAndGetNextMenu(car,1);
    }

    @Test
    @DisplayName("runProducedCar_정상여부2")
    void runProducedCar_정상여부2() throws InterruptedException {
        car.setEngine("TOYOTA");
        Menu result = sut.applyAndGetNextMenu(car,1);
    }

    @Test
    @DisplayName("runProducedCar_정상여부3")
    void runProducedCar_정상여부3() throws InterruptedException {
        car.setEngine("고장난 엔진");
        Menu result = sut.applyAndGetNextMenu(car,1);
    }

    @Test
    @DisplayName("testProducedCar_정상여부1")
    void testProducedCar_정상여부1() throws InterruptedException {
        Menu result = sut.applyAndGetNextMenu(car,2);
    }

    @Test
    @DisplayName("testProducedCar_정상여부2")
    void testProducedCar_정상여부2() throws InterruptedException {
        car.setEngine("TOYOTA");
        Menu result = sut.applyAndGetNextMenu(car,2);
    }













}