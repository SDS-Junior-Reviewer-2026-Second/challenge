import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class AssembleTest {

    private static final int CAR_TYPE_Q = 0;
    private static final int ENGINE_Q = 1;
    private static final int BRAKE_SYSTEM_Q = 2;
    private static final int STEERING_SYSTEM_Q = 3;
    private static final int RUN_TEST = 4;

    private static final int SEDAN = 1, SUV = 2, TRUCK = 3;
    private static final int GM = 1, TOYOTA = 2, WIA = 3, BROKEN_ENGINE = 4;
    private static final int MANDO = 1, CONTINENTAL = 2, BOSCH_BRAKE = 3;
    private static final int BOSCH_STEERING = 1, MOBIS = 2;

    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
        resetStack();
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private String output() {
        return capturedOut.toString(StandardCharsets.UTF_8);
    }

    // Feeds scripted input through the real Scanner in main(), exactly as a user
    // would type it, instead of reflecting into Assemble's state.
    private void runMainWith(String... lines) {
        String script = String.join("\n", lines) + "\n";
        System.setIn(new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)));
        Assemble.main(new String[0]);
    }

    // Assemble exposes no seam other than main() (no constructor, no public
    // methods, no injectable collaborators), so the only way to exercise its
    // range/rule checks individually - without paying for main()'s Thread.sleep
    // delays on every one of the many combinations below - is reflection.
    private int[] readStack() {
        try {
            Field field = Assemble.class.getDeclaredField("stack");
            field.setAccessible(true);
            return (int[]) field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetStack() {
        int[] stack = readStack();
        for (int i = 0; i < stack.length; i++) {
            stack[i] = 0;
        }
    }

    private void setStack(int carType, int engine, int brake, int steering) {
        int[] stack = readStack();
        stack[CAR_TYPE_Q] = carType;
        stack[ENGINE_Q] = engine;
        stack[BRAKE_SYSTEM_Q] = brake;
        stack[STEERING_SYSTEM_Q] = steering;
    }

    private Object invoke(String name, Class<?>[] paramTypes, Object... args) {
        try {
            Method method = Assemble.class.getDeclaredMethod(name, paramTypes);
            method.setAccessible(true);
            return method.invoke(null, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidRange(int step, int answer) {
        return (boolean) invoke(
            "isValidRange",
            new Class<?>[] { int.class, int.class },
            step,
            answer
        );
    }

    private boolean isValidCheck() {
        return (boolean) invoke("isValidCheck", new Class<?>[0]);
    }

    private void runProducedCar() {
        invoke("runProducedCar", new Class<?>[0]);
    }

    private void testProducedCar() {
        invoke("testProducedCar", new Class<?>[0]);
    }

    private void selectCarType(int answer) {
        invoke("selectCarType", new Class<?>[] { int.class }, answer);
    }

    private void selectEngine(int answer) {
        invoke("selectEngine", new Class<?>[] { int.class }, answer);
    }

    private void selectBrakeSystem(int answer) {
        invoke("selectBrakeSystem", new Class<?>[] { int.class }, answer);
    }

    private void selectSteeringSystem(int answer) {
        invoke("selectSteeringSystem", new Class<?>[] { int.class }, answer);
    }

    // ---- isValidRange: boundaries for every step ----

    @ParameterizedTest(name = "car type answer {0} -> valid={1}")
    @CsvSource({ "1,true", "2,true", "3,true", "0,false", "4,false", "-1,false" })
    void isValidRange_carType(int answer, boolean expected) {
        assertThat(isValidRange(CAR_TYPE_Q, answer)).isEqualTo(expected);
        if (!expected) {
            assertThat(output()).contains("차량 타입은 1 ~ 3 범위만 선택 가능");
        }
    }

    @ParameterizedTest(name = "engine answer {0} -> valid={1}")
    @CsvSource({ "0,true", "1,true", "2,true", "3,true", "4,true", "-1,false", "5,false" })
    void isValidRange_engine(int answer, boolean expected) {
        assertThat(isValidRange(ENGINE_Q, answer)).isEqualTo(expected);
        if (!expected) {
            assertThat(output()).contains("엔진은 1 ~ 4 범위만 선택 가능");
        }
    }

    @ParameterizedTest(name = "brake answer {0} -> valid={1}")
    @CsvSource({ "0,true", "1,true", "2,true", "3,true", "-1,false", "4,false" })
    void isValidRange_brake(int answer, boolean expected) {
        assertThat(isValidRange(BRAKE_SYSTEM_Q, answer)).isEqualTo(expected);
        if (!expected) {
            assertThat(output()).contains("제동장치는 1 ~ 3 범위만 선택 가능");
        }
    }

    @ParameterizedTest(name = "steering answer {0} -> valid={1}")
    @CsvSource({ "0,true", "1,true", "2,true", "-1,false", "3,false" })
    void isValidRange_steering(int answer, boolean expected) {
        assertThat(isValidRange(STEERING_SYSTEM_Q, answer)).isEqualTo(expected);
        if (!expected) {
            assertThat(output()).contains("조향장치는 1 ~ 2 범위만 선택 가능");
        }
    }

    @ParameterizedTest(name = "run/test answer {0} -> valid={1}")
    @CsvSource({ "0,true", "1,true", "2,true", "-1,false", "3,false" })
    void isValidRange_runTest(int answer, boolean expected) {
        assertThat(isValidRange(RUN_TEST, answer)).isEqualTo(expected);
        if (!expected) {
            assertThat(output()).contains("Run 또는 Test 중 하나를 선택 필요");
        }
    }

    // ---- select*: stack is updated and the right name is echoed ----

    @ParameterizedTest(name = "car type {0} -> {1}")
    @CsvSource({ "1,Sedan", "2,SUV", "3,Truck" })
    void selectCarType_updatesStackAndPrintsName(int answer, String name) {
        selectCarType(answer);
        assertThat(readStack()[CAR_TYPE_Q]).isEqualTo(answer);
        assertThat(output()).contains(name + "을 선택하셨습니다.");
    }

    @ParameterizedTest(name = "engine {0} -> {1}")
    @CsvSource({ "1,GM", "2,TOYOTA", "3,WIA", "4,'고장난 엔진'" })
    void selectEngine_updatesStackAndPrintsName(int answer, String name) {
        selectEngine(answer);
        assertThat(readStack()[ENGINE_Q]).isEqualTo(answer);
        assertThat(output()).contains(name + " 엔진을 선택하셨습니다.");
    }

    @ParameterizedTest(name = "brake {0} -> {1}")
    @CsvSource({ "1,MANDO", "2,CONTINENTAL", "3,BOSCH" })
    void selectBrakeSystem_updatesStackAndPrintsName(int answer, String name) {
        selectBrakeSystem(answer);
        assertThat(readStack()[BRAKE_SYSTEM_Q]).isEqualTo(answer);
        assertThat(output()).contains(name + " 제동장치를 선택하셨습니다.");
    }

    @ParameterizedTest(name = "steering {0} -> {1}")
    @CsvSource({ "1,BOSCH", "2,MOBIS" })
    void selectSteeringSystem_updatesStackAndPrintsName(int answer, String name) {
        selectSteeringSystem(answer);
        assertThat(readStack()[STEERING_SYSTEM_Q]).isEqualTo(answer);
        assertThat(output()).contains(name + " 조향장치를 선택하셨습니다.");
    }

    // ---- isValidCheck / testProducedCar / runProducedCar: part combinations ----

    static Stream<Arguments> incompatibleCombinations() {
        return Stream.of(
            Arguments.of(SEDAN, GM, CONTINENTAL, BOSCH_STEERING, "Sedan에는 Continental제동장치 사용 불가"),
            Arguments.of(SUV, TOYOTA, MANDO, BOSCH_STEERING, "SUV에는 TOYOTA엔진 사용 불가"),
            Arguments.of(TRUCK, WIA, CONTINENTAL, BOSCH_STEERING, "Truck에는 WIA엔진 사용 불가"),
            Arguments.of(TRUCK, GM, MANDO, BOSCH_STEERING, "Truck에는 Mando제동장치 사용 불가"),
            Arguments.of(SEDAN, GM, BOSCH_BRAKE, MOBIS, "Bosch제동장치에는 Bosch조향장치 이외 사용 불가")
        );
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("incompatibleCombinations")
    void isValidCheck_rejectsEachIncompatibleCombination(
        int carType,
        int engine,
        int brake,
        int steering,
        String reason
    ) {
        setStack(carType, engine, brake, steering);
        assertThat(isValidCheck()).isFalse();
    }

    @Test
    void isValidCheck_acceptsCompatibleCombination() {
        setStack(SEDAN, GM, MANDO, BOSCH_STEERING);
        assertThat(isValidCheck()).isTrue();
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("incompatibleCombinations")
    void testProducedCar_reportsSpecificFailReason(
        int carType,
        int engine,
        int brake,
        int steering,
        String reason
    ) {
        setStack(carType, engine, brake, steering);
        testProducedCar();
        assertThat(output()).contains("자동차 부품 조합 테스트 결과 : FAIL").contains(reason);
    }

    @Test
    void testProducedCar_reportsPassForCompatibleCombination() {
        setStack(SEDAN, GM, MANDO, BOSCH_STEERING);
        testProducedCar();
        assertThat(output()).contains("자동차 부품 조합 테스트 결과 : PASS");
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("incompatibleCombinations")
    void runProducedCar_refusesEachIncompatibleCombination(
        int carType,
        int engine,
        int brake,
        int steering,
        String reason
    ) {
        setStack(carType, engine, brake, steering);
        runProducedCar();
        assertThat(output()).contains("자동차가 동작되지 않습니다");
    }

    @Test
    void runProducedCar_refusesBrokenEngineEvenWhenPartsAreCompatible() {
        setStack(SEDAN, BROKEN_ENGINE, MANDO, BOSCH_STEERING);
        runProducedCar();
        assertThat(output()).contains("엔진이 고장나있습니다.").contains("자동차가 움직이지 않습니다.");
    }

    @Test
    void runProducedCar_runsForCompatibleCombination() {
        setStack(SUV, GM, BOSCH_BRAKE, BOSCH_STEERING);
        runProducedCar();
        assertThat(output())
            .contains("Car Type : SUV")
            .contains("Engine   : GM")
            .contains("Brake    : Bosch")
            .contains("Steering : Bosch")
            .contains("자동차가 동작됩니다.");
    }

    // ---- End-to-end flows driven through main() via real stdin/stdout ----

    @Test
    void mainFlow_recoversFromInvalidInputThenBuildsAndRunsCar() {
        runMainWith(
            "oops", // non-numeric input on the car type screen -> re-prompted
            "9", // out of range car type -> re-prompted
            "1", // Sedan
            "1", // GM
            "1", // MANDO
            "1", // BOSCH
            "1", // RUN
            "exit"
        );

        assertThat(output())
            .contains("ERROR :: 숫자만 입력 가능")
            .contains("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능")
            .contains("Sedan을 선택하셨습니다.")
            .contains("자동차가 동작됩니다.")
            .contains("바이바이");
    }

    @Test
    void mainFlow_goingBackChangesEarlierSelection() {
        runMainWith(
            "2", // SUV
            "1", // GM
            "0", // back to engine
            "3", // WIA (now SUV + WIA, still compatible)
            "1", // MANDO
            "1", // BOSCH
            "2", // Test
            "0", // back to start from run/test screen
            "exit"
        );

        assertThat(output())
            .contains("SUV을 선택하셨습니다.")
            .contains("GM 엔진을 선택하셨습니다.")
            .contains("WIA 엔진을 선택하셨습니다.")
            .contains("자동차 부품 조합 테스트 결과 : PASS")
            .contains("바이바이");
    }
}
