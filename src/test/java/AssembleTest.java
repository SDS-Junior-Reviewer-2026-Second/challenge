import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssembleTest {

    @Nested
    class 부품_타입과_완성_차량 {

        @Test
        void 모든_정상_코드는_대응하는_enum으로_변환된다() {
            assertThat(CarType.fromCode(1)).isEqualTo(CarType.SEDAN);
            assertThat(CarType.fromCode(2)).isEqualTo(CarType.SUV);
            assertThat(CarType.fromCode(3)).isEqualTo(CarType.TRUCK);

            assertThat(Engine.fromCode(1)).isEqualTo(Engine.GM);
            assertThat(Engine.fromCode(2)).isEqualTo(Engine.TOYOTA);
            assertThat(Engine.fromCode(3)).isEqualTo(Engine.WIA);
            assertThat(Engine.fromCode(4)).isEqualTo(Engine.BROKEN);

            assertThat(BrakeSystem.fromCode(1)).isEqualTo(BrakeSystem.MANDO);
            assertThat(BrakeSystem.fromCode(2)).isEqualTo(BrakeSystem.CONTINENTAL);
            assertThat(BrakeSystem.fromCode(3)).isEqualTo(BrakeSystem.BOSCH);

            assertThat(SteeringSystem.fromCode(1)).isEqualTo(SteeringSystem.BOSCH);
            assertThat(SteeringSystem.fromCode(2)).isEqualTo(SteeringSystem.MOBIS);
        }

        @Test
        void 정의되지_않은_코드는_예외를_발생시킨다() {
            assertThatThrownBy(() -> CarType.fromCode(0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> CarType.fromCode(4)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Engine.fromCode(0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Engine.fromCode(5)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> BrakeSystem.fromCode(0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> BrakeSystem.fromCode(4)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> SteeringSystem.fromCode(0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> SteeringSystem.fromCode(3)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 완성_차량은_모든_부품이_있어야_생성된다() {
            assertThatThrownBy(() -> new Car(null, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Car(CarType.SEDAN, null, BrakeSystem.MANDO, SteeringSystem.BOSCH))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Car(CarType.SEDAN, Engine.GM, null, SteeringSystem.BOSCH))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Car(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void 메뉴명과_RUN_표시명은_원본의_대소문자를_유지한다() {
            assertThat(BrakeSystem.MANDO.selectionName()).isEqualTo("MANDO");
            assertThat(BrakeSystem.MANDO.runName()).isEqualTo("Mando");
            assertThat(SteeringSystem.BOSCH.selectionName()).isEqualTo("BOSCH");
            assertThat(SteeringSystem.BOSCH.runName()).isEqualTo("Bosch");
        }
    }

    @Nested
    class 호환성_규칙과_RUN_TEST {
        private final CompatibilityPolicy policy = new CompatibilityPolicy();
        private final CarService service = new CarService(policy);

        @Test
        void 여러_규칙을_동시에_위반하면_원본_우선순위의_첫_오류만_반환한다() {
            Car car = 차량(CarType.TRUCK, Engine.WIA, BrakeSystem.MANDO, SteeringSystem.MOBIS);

            assertThat(policy.findViolation(car))
                    .contains("Truck에는 WIA엔진 사용 불가");
        }

        @Test
        void 고장난_엔진은_조합_테스트는_통과하지만_RUN할_수_없다() {
            Car car = 차량(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.BOSCH);

            assertThat(service.test(car)).isEmpty();
            assertThat(service.run(car)).isEqualTo(CarService.RunStatus.BROKEN_ENGINE);
        }

        @Test
        void RUN은_호환성_오류를_고장난_엔진보다_먼저_판단한다() {
            Car car = 차량(CarType.SEDAN, Engine.BROKEN, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);

            assertThat(service.run(car)).isEqualTo(CarService.RunStatus.INCOMPATIBLE);
        }
    }

    @Nested
    class 조립_단계 {

        @Test
        void 각_단계는_원본과_동일한_입력_범위를_허용한다() {
            assertThat(AssemblyStep.CAR_TYPE.accepts(0)).isFalse();
            assertThat(AssemblyStep.CAR_TYPE.accepts(1)).isTrue();
            assertThat(AssemblyStep.CAR_TYPE.accepts(3)).isTrue();
            assertThat(AssemblyStep.CAR_TYPE.accepts(4)).isFalse();

            assertThat(AssemblyStep.ENGINE.accepts(-1)).isFalse();
            assertThat(AssemblyStep.ENGINE.accepts(0)).isTrue();
            assertThat(AssemblyStep.ENGINE.accepts(4)).isTrue();
            assertThat(AssemblyStep.ENGINE.accepts(5)).isFalse();

            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(-1)).isFalse();
            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(0)).isTrue();
            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(3)).isTrue();
            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(4)).isFalse();

            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(-1)).isFalse();
            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(0)).isTrue();
            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(2)).isTrue();
            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(3)).isFalse();

            assertThat(AssemblyStep.RUN_TEST.accepts(-1)).isFalse();
            assertThat(AssemblyStep.RUN_TEST.accepts(0)).isTrue();
            assertThat(AssemblyStep.RUN_TEST.accepts(2)).isTrue();
            assertThat(AssemblyStep.RUN_TEST.accepts(3)).isFalse();
        }

        @Test
        void 다음_단계와_뒤로가기는_원본의_흐름을_유지한다() {
            assertThat(AssemblyStep.CAR_TYPE.nextStep()).isEqualTo(AssemblyStep.ENGINE);
            assertThat(AssemblyStep.ENGINE.nextStep()).isEqualTo(AssemblyStep.BRAKE_SYSTEM);
            assertThat(AssemblyStep.BRAKE_SYSTEM.nextStep()).isEqualTo(AssemblyStep.STEERING_SYSTEM);
            assertThat(AssemblyStep.STEERING_SYSTEM.nextStep()).isEqualTo(AssemblyStep.RUN_TEST);
            assertThat(AssemblyStep.RUN_TEST.nextStep()).isEqualTo(AssemblyStep.RUN_TEST);

            assertThat(AssemblyStep.ENGINE.backStep()).isEqualTo(AssemblyStep.CAR_TYPE);
            assertThat(AssemblyStep.BRAKE_SYSTEM.backStep()).isEqualTo(AssemblyStep.ENGINE);
            assertThat(AssemblyStep.STEERING_SYSTEM.backStep()).isEqualTo(AssemblyStep.BRAKE_SYSTEM);
            assertThat(AssemblyStep.RUN_TEST.backStep()).isEqualTo(AssemblyStep.CAR_TYPE);
            assertThatThrownBy(AssemblyStep.CAR_TYPE::backStep).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class 콘솔_표시 {

        @Test
        void 모든_메뉴와_선택_문구는_원본_형식을_유지한다() throws Exception {
            Capture capture = 캡처("");
            ConsoleView view = capture.view();

            for (AssemblyStep step : AssemblyStep.values()) {
                view.showMenu(step);
            }
            view.showCarTypeSelection(CarType.SUV);
            view.showEngineSelection(Engine.BROKEN);
            view.showBrakeSelection(BrakeSystem.MANDO);
            view.showSteeringSelection(SteeringSystem.BOSCH);

            assertThat(capture.output())
                    .contains("1. Sedan\n2. SUV\n3. Truck")
                    .contains("1. GM\n2. TOYOTA\n3. WIA\n4. 고장난 엔진")
                    .contains("1. MANDO\n2. CONTINENTAL\n3. BOSCH")
                    .contains("1. BOSCH\n2. MOBIS")
                    .contains("0. 처음 화면으로 돌아가기\n1. RUN\n2. Test")
                    .contains("차량 타입으로 SUV을 선택하셨습니다.\n")
                    .contains("고장난 엔진 엔진을 선택하셨습니다.\n")
                    .contains("MANDO 제동장치를 선택하셨습니다.\n")
                    .contains("BOSCH 조향장치를 선택하셨습니다.\n");
        }
    }

    @Nested
    class 전체_회귀 {

        @Test
        void 전체_72개_부품_조합의_RUN과_Test가_원본과_같다() throws Exception {
            int count = 0;

            for (CarType type : CarType.values()) {
                for (Engine engine : Engine.values()) {
                    for (BrakeSystem brake : BrakeSystem.values()) {
                        for (SteeringSystem steering : SteeringSystem.values()) {
                            String commands = type.code() + "\n"
                                    + engine.code() + "\n"
                                    + brake.code() + "\n"
                                    + steering.code() + "\n"
                                    + "1\n2\nexit\n";

                            Session result = 실행(commands);
                            String violation = 원본_실패사유(type, engine, brake, steering);

                            if (violation != null) {
                                assertThat(result.output())
                                        .as("%s/%s/%s/%s RUN", type, engine, brake, steering)
                                        .contains("자동차가 동작되지 않습니다\n");
                                assertThat(result.output())
                                        .as("%s/%s/%s/%s TEST", type, engine, brake, steering)
                                        .contains("자동차 부품 조합 테스트 결과 : FAIL\n" + violation + "\n");
                            } else if (engine == Engine.BROKEN) {
                                assertThat(result.output())
                                        .as("%s/%s/%s/%s RUN", type, engine, brake, steering)
                                        .contains("엔진이 고장나있습니다.\n자동차가 움직이지 않습니다.\n");
                                assertThat(result.output())
                                        .as("%s/%s/%s/%s TEST", type, engine, brake, steering)
                                        .contains("자동차 부품 조합 테스트 결과 : PASS\n");
                            } else {
                                String running = "Car Type : " + type.displayName() + "\n"
                                        + "Engine   : " + engine.displayName() + "\n"
                                        + "Brake    : " + brake.runName() + "\n"
                                        + "Steering : " + steering.runName() + "\n"
                                        + "자동차가 동작됩니다.\n";
                                assertThat(result.output())
                                        .as("%s/%s/%s/%s RUN", type, engine, brake, steering)
                                        .contains(running);
                                assertThat(result.output())
                                        .as("%s/%s/%s/%s TEST", type, engine, brake, steering)
                                        .contains("자동차 부품 조합 테스트 결과 : PASS\n");
                            }

                            assertThat(등장횟수(result.output(), "자동차 부품 조합 테스트 결과 : "))
                                    .as("%s/%s/%s/%s Test 실행 횟수", type, engine, brake, steering)
                                    .isEqualTo(1);

                            assertThat(result.delays())
                                    .as("%s/%s/%s/%s delay", type, engine, brake, steering)
                                    .containsExactly(800, 800, 800, 800, 2000, 1500, 2000);

                            count++;
                        }
                    }
                }
            }

            assertThat(count).isEqualTo(72);
        }

        @Test
        void 잘못된_입력과_모든_뒤로가기_경로를_처리한다() throws Exception {
            String commands = String.join("\n",
                    "abc", "0", "4", "1",
                    "0", "2", "5", "1",
                    "0", "3", "4", "1",
                    "0", "2", "3", "1",
                    "0", "1", "1", "1", "1", "3", "1",
                    "exit") + "\n";

            Session result = 실행(commands);

            assertThat(result.output())
                    .contains("ERROR :: 숫자만 입력 가능")
                    .contains("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능")
                    .contains("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능")
                    .contains("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능")
                    .contains("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능")
                    .contains("ERROR :: Run 또는 Test 중 하나를 선택 필요")
                    .contains("자동차가 동작됩니다.")
                    .endsWith("바이바이\n");

            assertThat(등장횟수(result.output(), "어떤 차량 타입을 선택할까요?")).isGreaterThanOrEqualTo(3);
            assertThat(등장횟수(result.output(), "어떤 엔진을 탑재할까요?")).isGreaterThanOrEqualTo(3);
            assertThat(등장횟수(result.output(), "어떤 제동장치를 선택할까요?")).isGreaterThanOrEqualTo(2);
            assertThat(등장횟수(result.output(), "어떤 조향장치를 선택할까요?")).isGreaterThanOrEqualTo(2);
        }

        @Test
        void RUN과_Test를_같은_차량으로_반복할_수_있다() throws Exception {
            Session result = 실행("1\n1\n1\n1\n1\n2\n1\n2\nexit\n");

            assertThat(등장횟수(result.output(), "자동차가 동작됩니다.")).isEqualTo(2);
            assertThat(등장횟수(result.output(), "자동차 부품 조합 테스트 결과 : PASS")).isEqualTo(2);
            assertThat(result.delays())
                    .containsExactly(800, 800, 800, 800, 2000, 1500, 2000, 2000, 1500, 2000);
        }

        @Test
        void RUN_Test화면에서_처음으로_돌아가면_새_차량을_조립할_수_있다() throws Exception {
            Session result = 실행("1\n4\n1\n1\n1\n0\n3\n2\n2\n1\n1\nexit\n");

            assertThat(result.output())
                    .contains("엔진이 고장나있습니다.\n자동차가 움직이지 않습니다.\n")
                    .contains("Car Type : Truck\nEngine   : TOYOTA\nBrake    : Continental\nSteering : Bosch\n자동차가 동작됩니다.\n");
        }
    }

    @Nested
    class 프로그램_진입점 {

        @Test
        void main은_delay가_interrupt되어도_원본처럼_계속_실행한다() throws Exception {
            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            try {
                System.setIn(new ByteArrayInputStream("abc\nexit\n".getBytes(StandardCharsets.UTF_8)));
                System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
                Thread.currentThread().interrupt();

                Assemble.main(new String[0]);
            } finally {
                Thread.interrupted();
                System.setIn(originalIn);
                System.setOut(originalOut);
            }

            assertThat(bytes.toString(StandardCharsets.UTF_8))
                    .contains("ERROR :: 숫자만 입력 가능")
                    .contains("바이바이");
        }
    }

    private static Car 차량(CarType type, Engine engine,
                          BrakeSystem brake, SteeringSystem steering) {
        return new Car(type, engine, brake, steering);
    }

    private static String 원본_실패사유(CarType type, Engine engine,
                                  BrakeSystem brake, SteeringSystem steering) {
        if (type == CarType.SEDAN && brake == BrakeSystem.CONTINENTAL) {
            return "Sedan에는 Continental제동장치 사용 불가";
        }
        if (type == CarType.SUV && engine == Engine.TOYOTA) {
            return "SUV에는 TOYOTA엔진 사용 불가";
        }
        if (type == CarType.TRUCK && engine == Engine.WIA) {
            return "Truck에는 WIA엔진 사용 불가";
        }
        if (type == CarType.TRUCK && brake == BrakeSystem.MANDO) {
            return "Truck에는 Mando제동장치 사용 불가";
        }
        if (brake == BrakeSystem.BOSCH && steering != SteeringSystem.BOSCH) {
            return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";
        }
        return null;
    }

    private static Session 실행(String commands) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        List<Integer> delays = new ArrayList<>();

        try (Scanner scanner = new Scanner(commands);
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            ConsoleView view = new ConsoleView(scanner, output);
            CarService service = new CarService(new CompatibilityPolicy());
            new AssemblyProgram(view, service, delays::add).run();
        }

        return new Session(
                bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"),
                delays
        );
    }

    private static Capture 캡처(String input) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Scanner scanner = new Scanner(input);
        PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        return new Capture(new ConsoleView(scanner, output), bytes);
    }

    private static int 등장횟수(String text, String token) {
        return (text.length() - text.replace(token, "").length()) / token.length();
    }

    private record Session(String output, List<Integer> delays) {
    }

    private record Capture(ConsoleView view, ByteArrayOutputStream bytes) {
        String output() {
            return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        }
    }
}
