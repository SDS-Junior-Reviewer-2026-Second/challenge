import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AssembleTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws Exception {
        originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        setStack(0, 0, 0, 0);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("각 단계는 정의된 입력 범위만 허용한다")
    void validatesEveryMenuRange() throws Exception {
        assertThat(isValidRange(0, 1)).isTrue();
        assertThat(isValidRange(0, 3)).isTrue();
        assertThat(isValidRange(0, 0)).isFalse();
        assertThat(isValidRange(0, 4)).isFalse();

        assertThat(isValidRange(1, 0)).isTrue();
        assertThat(isValidRange(1, 4)).isTrue();
        assertThat(isValidRange(1, -1)).isFalse();
        assertThat(isValidRange(1, 5)).isFalse();

        assertThat(isValidRange(2, 0)).isTrue();
        assertThat(isValidRange(2, 3)).isTrue();
        assertThat(isValidRange(2, 4)).isFalse();

        assertThat(isValidRange(3, 0)).isTrue();
        assertThat(isValidRange(3, 2)).isTrue();
        assertThat(isValidRange(3, 3)).isFalse();

        assertThat(isValidRange(4, 0)).isTrue();
        assertThat(isValidRange(4, 2)).isTrue();
        assertThat(isValidRange(4, 3)).isFalse();
    }

    @Test
    @DisplayName("부품 선택은 조립 상태에 저장된다")
    void selectionsAreStored() throws Exception {
        invoke("selectCarType", new Class<?>[]{int.class}, 2);
        invoke("selectEngine", new Class<?>[]{int.class}, 3);
        invoke("selectBrakeSystem", new Class<?>[]{int.class}, 1);
        invoke("selectSteeringSystem", new Class<?>[]{int.class}, 2);

        assertThat(stack()).containsExactly(2, 3, 1, 2, 0);
    }

    @Test
    @DisplayName("허용되는 부품 조합은 유효하다")
    void acceptsCompatibleCombination() throws Exception {
        setStack(1, 1, 1, 2);

        assertThat(isValidCombination()).isTrue();
    }

    @Test
    @DisplayName("Sedan과 Continental 브레이크 조합은 거부한다")
    void rejectsSedanWithContinentalBrake() throws Exception {
        assertInvalidCombination(1, 1, 2, 1, "Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    @DisplayName("SUV와 Toyota 엔진 조합은 거부한다")
    void rejectsSuvWithToyotaEngine() throws Exception {
        assertInvalidCombination(2, 2, 1, 1, "SUV에는 TOYOTA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck과 WIA 엔진 조합은 거부한다")
    void rejectsTruckWithWiaEngine() throws Exception {
        assertInvalidCombination(3, 3, 3, 1, "Truck에는 WIA엔진 사용 불가");
    }

    @Test
    @DisplayName("Truck과 Mando 브레이크 조합은 거부한다")
    void rejectsTruckWithMandoBrake() throws Exception {
        assertInvalidCombination(3, 1, 1, 1, "Truck에는 Mando제동장치 사용 불가");
    }

    @Test
    @DisplayName("Bosch 브레이크에는 Bosch 조향장치만 허용한다")
    void rejectsBoschBrakeWithNonBoschSteering() throws Exception {
        assertInvalidCombination(1, 1, 3, 2, "Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
    }

    @Test
    @DisplayName("정상 차량 실행 시 선택 사양과 동작 메시지를 출력한다")
    void runsValidCar() throws Exception {
        setStack(2, 3, 2, 2);

        invoke("runProducedCar");

        assertThat(console())
                .contains("Car Type : SUV")
                .contains("Engine   : WIA")
                .contains("Brake    : Continental")
                .contains("Steering : Mobis")
                .contains("자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("고장난 엔진 차량은 움직이지 않는다")
    void doesNotRunWithBrokenEngine() throws Exception {
        setStack(1, 4, 1, 1);

        invoke("runProducedCar");

        assertThat(console())
                .contains("엔진이 고장나있습니다.")
                .contains("자동차가 움직이지 않습니다.")
                .doesNotContain("자동차가 동작됩니다.");
    }

    @Test
    @DisplayName("유효하지 않은 조합의 차량은 실행되지 않는다")
    void doesNotRunInvalidCombination() throws Exception {
        setStack(1, 1, 2, 1);

        invoke("runProducedCar");

        assertThat(console()).contains("자동차가 동작되지 않습니다");
    }

    @Test
    @DisplayName("유효한 조합 테스트는 PASS를 출력한다")
    void reportsPassForValidCombination() throws Exception {
        setStack(1, 1, 1, 2);

        invoke("testProducedCar");

        assertThat(console()).contains("자동차 부품 조합 테스트 결과 : PASS");
    }

    private void assertInvalidCombination(int car, int engine, int brake, int steering, String reason)
            throws Exception {
        setStack(car, engine, brake, steering);

        assertThat(isValidCombination()).isFalse();
        invoke("testProducedCar");
        assertThat(console())
                .contains("자동차 부품 조합 테스트 결과 : FAIL")
                .contains(reason);
    }

    private boolean isValidRange(int step, int answer) throws Exception {
        return (boolean) invoke("isValidRange", new Class<?>[]{int.class, int.class}, step, answer);
    }

    private boolean isValidCombination() throws Exception {
        return (boolean) invoke("isValidCheck");
    }

    private void setStack(int car, int engine, int brake, int steering) throws Exception {
        Field field = Assemble.class.getDeclaredField("stack");
        field.setAccessible(true);
        field.set(null, new int[]{car, engine, brake, steering, 0});
    }

    private int[] stack() throws Exception {
        Field field = Assemble.class.getDeclaredField("stack");
        field.setAccessible(true);
        return (int[]) field.get(null);
    }

    private Object invoke(String name) throws Exception {
        return invoke(name, new Class<?>[0]);
    }

    private Object invoke(String name, Class<?>[] parameterTypes, Object... arguments) throws Exception {
        Method method = Assemble.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(null, arguments);
    }

    private String console() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
