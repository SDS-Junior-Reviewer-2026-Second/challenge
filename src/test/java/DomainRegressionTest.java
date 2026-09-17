import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DomainRegressionTest {
    private final CarService service = new CarService();

    public static void main(String[] args) throws Exception {
        DomainRegressionTest test = new DomainRegressionTest();
        test.호환성_오류는_기존_우선순위를_유지한다();
        test.고장난_엔진은_조합_테스트를_통과하지만_주행할_수_없다();
        test.정상_차량의_RUN_출력은_기존과_동일하다();
        test.전체_72개_부품_조합을_평가할_수_있다();
        test.콘솔_흐름에서_입력검증_뒤로가기_RUN_Test를_모두_처리한다();
        test.모든_정상_부품_코드는_enum으로_변환된다();
        test.잘못된_부품_코드는_예외를_발생시킨다();
        test.메인_진입점은_실제_delay의_인터럽트와_exit을_처리한다();
        System.out.println("DomainRegressionTest: PASS");
    }

    private void 호환성_오류는_기존_우선순위를_유지한다() {
        CarConfiguration car = 차량(CarType.SEDAN, Engine.TOYOTA, BrakeSystem.CONTINENTAL, SteeringSystem.MOBIS);
        assertEquals(Arrays.asList(
                "자동차 부품 조합 테스트 결과 : FAIL",
                "Sedan에는 Continental제동장치 사용 불가"), service.test(car));
    }

    private void 고장난_엔진은_조합_테스트를_통과하지만_주행할_수_없다() {
        CarConfiguration car = 차량(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        assertEquals(Arrays.asList("자동차 부품 조합 테스트 결과 : PASS"), service.test(car));
        assertEquals(Arrays.asList("엔진이 고장나있습니다.", "자동차가 움직이지 않습니다."), service.run(car));
    }

    private void 정상_차량의_RUN_출력은_기존과_동일하다() {
        CarConfiguration car = 차량(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        List<String> expected = Arrays.asList(
                "Car Type : Sedan",
                "Engine   : GM",
                "Brake    : Mando",
                "Steering : Bosch",
                "자동차가 동작됩니다.");
        assertEquals(expected, service.run(car));
    }

    private void 전체_72개_부품_조합을_평가할_수_있다() {
        int count = 0;
        for (CarType carType : CarType.values()) {
            for (Engine engine : Engine.values()) {
                for (BrakeSystem brake : BrakeSystem.values()) {
                    for (SteeringSystem steering : SteeringSystem.values()) {
                        CarConfiguration car = 차량(carType, engine, brake, steering);
                        service.test(car);
                        service.run(car);
                        count++;
                    }
                }
            }
        }
        assertEquals(72, count);
    }

    private void 콘솔_흐름에서_입력검증_뒤로가기_RUN_Test를_모두_처리한다() throws Exception {
        String input = String.join("\n",
                "abc", "-1", "9", // 숫자 아님 / 최솟값 미만 / 최댓값 초과
                "1",                // Sedan
                "9", "0",          // 엔진 범위 초과 / 차량 선택으로 뒤로가기
                "2",                // SUV
                "1",                // GM
                "9", "0",          // 제동장치 범위 초과 / 엔진으로 뒤로가기
                "2",                // TOYOTA
                "1",                // MANDO
                "9", "0",          // 조향장치 범위 초과 / 제동장치로 뒤로가기
                "2",                // CONTINENTAL
                "1",                // BOSCH steering
                "9",                // RUN/Test 범위 초과
                "2",                // Test -> SUV + TOYOTA 실패
                "0",                // 처음 화면으로
                "1", "1", "1", "1", // 정상 Sedan/GM/MANDO/BOSCH
                "1",                // RUN
                "exit") + "\n";

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Scanner scanner = new Scanner(input);
        Assemble program = new Assemble(scanner, new PrintStream(buffer, true, "UTF-8"), ignored -> { });
        program.run();
        scanner.close();

        String output = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        assertContains(output, "ERROR :: 숫자만 입력 가능");
        assertContains(output, "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
        assertContains(output, "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
        assertContains(output, "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
        assertContains(output, "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
        assertContains(output, "ERROR :: Run 또는 Test 중 하나를 선택 필요");
        assertContains(output, "SUV에는 TOYOTA엔진 사용 불가");
        assertContains(output, "자동차가 동작됩니다.");
        assertContains(output, "바이바이");
    }

    private void 모든_정상_부품_코드는_enum으로_변환된다() {
        assertEquals(CarType.SEDAN, CarType.fromCode(1));
        assertEquals(CarType.SUV, CarType.fromCode(2));
        assertEquals(CarType.TRUCK, CarType.fromCode(3));

        assertEquals(Engine.GM, Engine.fromCode(1));
        assertEquals(Engine.TOYOTA, Engine.fromCode(2));
        assertEquals(Engine.WIA, Engine.fromCode(3));
        assertEquals(Engine.BROKEN, Engine.fromCode(4));

        assertEquals(BrakeSystem.MANDO, BrakeSystem.fromCode(1));
        assertEquals(BrakeSystem.CONTINENTAL, BrakeSystem.fromCode(2));
        assertEquals(BrakeSystem.BOSCH, BrakeSystem.fromCode(3));

        assertEquals(SteeringSystem.BOSCH, SteeringSystem.fromCode(1));
        assertEquals(SteeringSystem.MOBIS, SteeringSystem.fromCode(2));
    }

    private void 잘못된_부품_코드는_예외를_발생시킨다() {
        assertIllegalArgument(() -> CarType.fromCode(0));
        assertIllegalArgument(() -> Engine.fromCode(0));
        assertIllegalArgument(() -> BrakeSystem.fromCode(0));
        assertIllegalArgument(() -> SteeringSystem.fromCode(0));
    }

    private void 메인_진입점은_실제_delay의_인터럽트와_exit을_처리한다() throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream("abc\nexit\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(buffer, true, "UTF-8"));

            // 첫 오류 처리에서 실제 Thread.sleep()이 즉시 InterruptedException을 발생시키게 한다.
            Thread.currentThread().interrupt();
            Assemble.main(new String[0]);
        } finally {
            Thread.interrupted(); // 혹시 남아 있을 수 있는 interrupt 상태 정리
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        assertContains(output, "ERROR :: 숫자만 입력 가능");
        assertContains(output, "바이바이");
    }

    private CarConfiguration 차량(CarType carType, Engine engine, BrakeSystem brake, SteeringSystem steering) {
        CarConfiguration car = new CarConfiguration();
        car.selectCarType(carType);
        car.selectEngine(engine);
        car.selectBrakeSystem(brake);
        car.selectSteeringSystem(steering);
        return car;
    }

    private static void assertIllegalArgument(Runnable action) {
        try {
            action.run();
            throw new AssertionError("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    private static void assertContains(String actual, String expectedFragment) {
        if (!actual.contains(expectedFragment)) {
            throw new AssertionError("missing fragment=" + expectedFragment);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
        }
    }
}
