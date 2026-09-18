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
    class 부품_타입 {

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
            assertThatThrownBy(() -> CarType.fromCode(0))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> Engine.fromCode(0))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> BrakeSystem.fromCode(0))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> SteeringSystem.fromCode(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 부품의_메뉴명과_RUN_표시명은_원본과_같다() {
            assertThat(CarType.SEDAN.code()).isEqualTo(1);
            assertThat(CarType.SEDAN.displayName()).isEqualTo("Sedan");

            assertThat(Engine.BROKEN.code()).isEqualTo(4);
            assertThat(Engine.BROKEN.displayName()).isEqualTo("고장난 엔진");

            assertThat(BrakeSystem.MANDO.selectionName()).isEqualTo("MANDO");
            assertThat(BrakeSystem.MANDO.runName()).isEqualTo("Mando");

            assertThat(SteeringSystem.BOSCH.selectionName()).isEqualTo("BOSCH");
            assertThat(SteeringSystem.BOSCH.runName()).isEqualTo("Bosch");
        }
    }

    @Nested
    class 호환성_규칙 {

        private final CompatibilityPolicy policy = new CompatibilityPolicy();

        @Test
        void 전체_72개_부품_조합은_원본과_같은_호환성_결과를_반환한다() {
            int count = 0;

            for (CarType carType : CarType.values()) {
                for (Engine engine : Engine.values()) {
                    for (BrakeSystem brake : BrakeSystem.values()) {
                        for (SteeringSystem steering : SteeringSystem.values()) {
                            CarConfiguration car =
                                    차량(carType, engine, brake, steering);

                            String expected =
                                    원본_실패사유(
                                            carType,
                                            engine,
                                            brake,
                                            steering
                                    );

                            assertThat(policy.findViolation(car))
                                    .as(
                                            "%s / %s / %s / %s",
                                            carType,
                                            engine,
                                            brake,
                                            steering
                                    )
                                    .isEqualTo(
                                            Optional.ofNullable(expected)
                                    );

                            count++;
                        }
                    }
                }
            }

            assertThat(count).isEqualTo(72);
        }

        @Test
        void 여러_규칙을_동시에_위반하면_원본_우선순위의_첫_오류만_반환한다() {
            CarConfiguration car = 차량(
                    CarType.TRUCK,
                    Engine.WIA,
                    BrakeSystem.MANDO,
                    SteeringSystem.MOBIS
            );

            assertThat(policy.findViolation(car))
                    .contains("Truck에는 WIA엔진 사용 불가");
        }
    }

    @Nested
    class 자동차_RUN과_TEST {

        private final CarService service =
                new CarService(new CompatibilityPolicy());

        @Test
        void 정상_조합은_RUNNING이다() {
            CarConfiguration car = 차량(
                    CarType.SUV,
                    Engine.GM,
                    BrakeSystem.CONTINENTAL,
                    SteeringSystem.MOBIS
            );

            assertThat(service.run(car))
                    .isEqualTo(CarService.RunStatus.RUNNING);
        }

        @Test
        void 금지_조합은_INCOMPATIBLE이다() {
            CarConfiguration car = 차량(
                    CarType.SEDAN,
                    Engine.GM,
                    BrakeSystem.CONTINENTAL,
                    SteeringSystem.BOSCH
            );

            assertThat(service.run(car))
                    .isEqualTo(CarService.RunStatus.INCOMPATIBLE);
        }

        @Test
        void 호환되는_고장난_엔진은_BROKEN_ENGINE이고_TEST는_통과한다() {
            CarConfiguration car = 차량(
                    CarType.SEDAN,
                    Engine.BROKEN,
                    BrakeSystem.MANDO,
                    SteeringSystem.BOSCH
            );

            assertThat(service.run(car))
                    .isEqualTo(CarService.RunStatus.BROKEN_ENGINE);

            assertThat(service.test(car))
                    .isEmpty();
        }

        @Test
        void 호환성_오류는_고장난_엔진보다_먼저_판단한다() {
            CarConfiguration car = 차량(
                    CarType.SEDAN,
                    Engine.BROKEN,
                    BrakeSystem.CONTINENTAL,
                    SteeringSystem.BOSCH
            );

            assertThat(service.run(car))
                    .isEqualTo(CarService.RunStatus.INCOMPATIBLE);
        }

        @Test
        void TEST는_금지_조합의_첫_실패_사유를_반환한다() {
            CarConfiguration car = 차량(
                    CarType.TRUCK,
                    Engine.WIA,
                    BrakeSystem.MANDO,
                    SteeringSystem.MOBIS
            );

            assertThat(service.test(car))
                    .contains("Truck에는 WIA엔진 사용 불가");
        }
    }

    @Nested
    class 조립_단계 {

        @Test
        void 각_단계는_원본과_동일한_입력_범위를_허용한다() {
            assertThat(AssemblyStep.CAR_TYPE.accepts(0))
                    .isFalse();
            assertThat(AssemblyStep.CAR_TYPE.accepts(1))
                    .isTrue();
            assertThat(AssemblyStep.CAR_TYPE.accepts(3))
                    .isTrue();
            assertThat(AssemblyStep.CAR_TYPE.accepts(4))
                    .isFalse();

            assertThat(AssemblyStep.ENGINE.accepts(-1))
                    .isFalse();
            assertThat(AssemblyStep.ENGINE.accepts(0))
                    .isTrue();
            assertThat(AssemblyStep.ENGINE.accepts(4))
                    .isTrue();
            assertThat(AssemblyStep.ENGINE.accepts(5))
                    .isFalse();

            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(-1))
                    .isFalse();
            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(0))
                    .isTrue();
            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(3))
                    .isTrue();
            assertThat(AssemblyStep.BRAKE_SYSTEM.accepts(4))
                    .isFalse();

            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(-1))
                    .isFalse();
            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(0))
                    .isTrue();
            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(2))
                    .isTrue();
            assertThat(AssemblyStep.STEERING_SYSTEM.accepts(3))
                    .isFalse();

            assertThat(AssemblyStep.RUN_TEST.accepts(-1))
                    .isFalse();
            assertThat(AssemblyStep.RUN_TEST.accepts(0))
                    .isTrue();
            assertThat(AssemblyStep.RUN_TEST.accepts(2))
                    .isTrue();
            assertThat(AssemblyStep.RUN_TEST.accepts(3))
                    .isFalse();
        }

        @Test
        void 뒤로가기는_원본의_단계_이동을_유지한다() {
            assertThat(
                    AssemblyStep.ENGINE.backStep()
            ).isEqualTo(
                    AssemblyStep.CAR_TYPE
            );

            assertThat(
                    AssemblyStep.BRAKE_SYSTEM.backStep()
            ).isEqualTo(
                    AssemblyStep.ENGINE
            );

            assertThat(
                    AssemblyStep.STEERING_SYSTEM.backStep()
            ).isEqualTo(
                    AssemblyStep.BRAKE_SYSTEM
            );

            assertThat(
                    AssemblyStep.RUN_TEST.backStep()
            ).isEqualTo(
                    AssemblyStep.CAR_TYPE
            );

            assertThatThrownBy(
                    AssemblyStep.CAR_TYPE::backStep
            ).isInstanceOf(
                    IllegalStateException.class
            );
        }

        @Test
        void 각_단계의_오류_메시지는_원본과_동일하다() {
            assertThat(
                    AssemblyStep.CAR_TYPE.errorMessage()
            ).isEqualTo(
                    "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능"
            );

            assertThat(
                    AssemblyStep.ENGINE.errorMessage()
            ).isEqualTo(
                    "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능"
            );

            assertThat(
                    AssemblyStep.BRAKE_SYSTEM.errorMessage()
            ).isEqualTo(
                    "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능"
            );

            assertThat(
                    AssemblyStep.STEERING_SYSTEM.errorMessage()
            ).isEqualTo(
                    "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능"
            );

            assertThat(
                    AssemblyStep.RUN_TEST.errorMessage()
            ).isEqualTo(
                    "ERROR :: Run 또는 Test 중 하나를 선택 필요"
            );
        }
    }

    @Nested
    class 콘솔_표시 {

        @Test
        void 모든_메뉴는_enum의_선택지와_원본_문구를_출력한다()
                throws Exception {

            Capture capture = 캡처("");
            ConsoleView view = capture.view();

            for (AssemblyStep step : AssemblyStep.values()) {
                view.showMenu(step);
            }

            assertThat(capture.output())
                    .contains(
                            "1. Sedan\n" +
                                    "2. SUV\n" +
                                    "3. Truck"
                    )
                    .contains(
                            "1. GM\n" +
                                    "2. TOYOTA\n" +
                                    "3. WIA\n" +
                                    "4. 고장난 엔진"
                    )
                    .contains(
                            "1. MANDO\n" +
                                    "2. CONTINENTAL\n" +
                                    "3. BOSCH"
                    )
                    .contains(
                            "1. BOSCH\n" +
                                    "2. MOBIS"
                    )
                    .contains(
                            "0. 처음 화면으로 돌아가기\n" +
                                    "1. RUN\n" +
                                    "2. Test"
                    );
        }

        @Test
        void 선택_메시지는_원본의_대소문자를_유지한다()
                throws Exception {

            Capture capture = 캡처("");
            ConsoleView view = capture.view();

            view.showCarTypeSelection(CarType.SUV);
            view.showEngineSelection(Engine.BROKEN);
            view.showBrakeSelection(BrakeSystem.MANDO);
            view.showSteeringSelection(SteeringSystem.BOSCH);

            assertThat(capture.output())
                    .contains(
                            "차량 타입으로 SUV을 선택하셨습니다.\n"
                    )
                    .contains(
                            "고장난 엔진 엔진을 선택하셨습니다.\n"
                    )
                    .contains(
                            "MANDO 제동장치를 선택하셨습니다.\n"
                    )
                    .contains(
                            "BOSCH 조향장치를 선택하셨습니다.\n"
                    );
        }

        @Test
        void RUN_결과_세_종류를_원본과_동일하게_표시한다()
                throws Exception {

            Capture capture = 캡처("");
            ConsoleView view = capture.view();

            CarConfiguration car = 차량(
                    CarType.SUV,
                    Engine.GM,
                    BrakeSystem.CONTINENTAL,
                    SteeringSystem.MOBIS
            );

            view.showRunResult(
                    car,
                    CarService.RunStatus.RUNNING
            );

            view.showRunResult(
                    car,
                    CarService.RunStatus.INCOMPATIBLE
            );

            view.showRunResult(
                    car,
                    CarService.RunStatus.BROKEN_ENGINE
            );

            assertThat(capture.output())
                    .contains("Car Type : SUV\n")
                    .contains("Engine   : GM\n")
                    .contains("Brake    : Continental\n")
                    .contains("Steering : Mobis\n")
                    .contains("자동차가 동작됩니다.")
                    .contains("자동차가 동작되지 않습니다")
                    .contains("엔진이 고장나있습니다.")
                    .contains("자동차가 움직이지 않습니다.");
        }

        @Test
        void TEST_결과는_PASS와_FAIL을_구분해_표시한다()
                throws Exception {

            Capture capture = 캡처("");
            ConsoleView view = capture.view();

            view.showTestStarted();

            view.showTestResult(
                    Optional.empty()
            );

            view.showTestResult(
                    Optional.of("실패 사유")
            );

            view.showInputError(
                    "ERROR :: 테스트"
            );

            view.showGoodbye();

            assertThat(capture.output())
                    .contains("Test...")
                    .contains(
                            "자동차 부품 조합 테스트 결과 : PASS"
                    )
                    .contains(
                            "자동차 부품 조합 테스트 결과 : FAIL\n" +
                                    "실패 사유"
                    )
                    .contains(
                            "ERROR :: 테스트"
                    )
                    .endsWith(
                            "바이바이\n"
                    );
        }

        @Test
        void 입력은_원본처럼_양쪽_공백을_제거한다()
                throws Exception {

            Capture capture =
                    캡처("  ExIt  \n");

            assertThat(
                    capture.view().readInput()
            ).isEqualTo(
                    "ExIt"
            );

            assertThat(
                    capture.output()
            ).isEqualTo(
                    "INPUT > "
            );
        }
    }

    @Nested
    class 전체_조립_흐름 {

        @Test
        void 오류_뒤로가기_RUN_Test_exit의_전체_흐름을_처리한다()
                throws Exception {

            String commands = String.join(
                    "\n",

                    "abc",
                    "-1",
                    "4",
                    "1",

                    "-1",
                    "5",
                    "0",

                    "2",
                    "2",

                    "-1",
                    "4",
                    "1",

                    "-1",
                    "3",
                    "1",

                    "-1",
                    "3",
                    "2",

                    "0",

                    "1",
                    "1",
                    "1",
                    "1",
                    "1",

                    "2",

                    "exit"
            ) + "\n";

            Session session =
                    실행(commands);

            assertThat(session.output())
                    .contains(
                            "ERROR :: 숫자만 입력 가능"
                    )
                    .contains(
                            "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능"
                    )
                    .contains(
                            "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능"
                    )
                    .contains(
                            "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능"
                    )
                    .contains(
                            "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능"
                    )
                    .contains(
                            "ERROR :: Run 또는 Test 중 하나를 선택 필요"
                    )
                    .contains(
                            "SUV에는 TOYOTA엔진 사용 불가"
                    )
                    .contains(
                            "자동차가 동작됩니다."
                    )
                    .contains(
                            "자동차 부품 조합 테스트 결과 : PASS"
                    )
                    .endsWith(
                            "바이바이\n"
                    );

            assertThat(
                    session.delays()
            ).contains(
                    800,
                    1500,
                    2000
            );
        }

        @Test
        void 엔진_제동장치_조향장치에서_각각_이전_단계로_돌아갈_수_있다()
                throws Exception {

            Session session = 실행(
                    String.join(
                            "\n",

                            "1",

                            "1",
                            "0",

                            "2",
                            "1",
                            "0",

                            "2",
                            "1",
                            "0",

                            "exit"
                    ) + "\n"
            );

            assertThat(
                    등장횟수(
                            session.output(),
                            "어떤 엔진을 탑재할까요?"
                    )
            ).isGreaterThanOrEqualTo(
                    2
            );

            assertThat(
                    등장횟수(
                            session.output(),
                            "어떤 제동장치를 선택할까요?"
                    )
            ).isGreaterThanOrEqualTo(
                    2
            );

            assertThat(
                    등장횟수(
                            session.output(),
                            "어떤 차량 타입을 선택할까요?"
                    )
            ).isGreaterThanOrEqualTo(
                    2
            );
        }

        @Test
        void RUN은_호환성_오류를_고장난_엔진보다_먼저_판단한다()
                throws Exception {

            Session session =
                    실행(
                            "1\n" +
                                    "4\n" +
                                    "2\n" +
                                    "1\n" +
                                    "1\n" +
                                    "exit\n"
                    );

            assertThat(session.output())
                    .contains(
                            "자동차가 동작되지 않습니다"
                    )
                    .doesNotContain(
                            "엔진이 고장나있습니다."
                    );
        }

        @Test
        void 고장난_엔진은_호환되는_경우에만_엔진_고장으로_RUN을_중단한다()
                throws Exception {

            Session session =
                    실행(
                            "1\n" +
                                    "4\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1\n" +
                                    "exit\n"
                    );

            assertThat(session.output())
                    .contains(
                            "엔진이 고장나있습니다."
                    )
                    .contains(
                            "자동차가 움직이지 않습니다."
                    );
        }
    }

    @Nested
    class 프로그램_진입점 {

        @Test
        void main은_실제_delay가_interrupt되어도_원본처럼_계속_실행한다()
                throws Exception {

            InputStream originalIn =
                    System.in;

            PrintStream originalOut =
                    System.out;

            ByteArrayOutputStream bytes =
                    new ByteArrayOutputStream();

            try {
                System.setIn(
                        new ByteArrayInputStream(
                                "abc\nexit\n"
                                        .getBytes(
                                                StandardCharsets.UTF_8
                                        )
                        )
                );

                System.setOut(
                        new PrintStream(
                                bytes,
                                true,
                                StandardCharsets.UTF_8
                        )
                );

                Thread.currentThread()
                        .interrupt();

                Assemble.main(
                        new String[0]
                );

            } finally {
                Thread.interrupted();

                System.setIn(
                        originalIn
                );

                System.setOut(
                        originalOut
                );
            }

            assertThat(
                    bytes.toString(
                            StandardCharsets.UTF_8
                    )
            )
                    .contains(
                            "ERROR :: 숫자만 입력 가능"
                    )
                    .contains(
                            "바이바이"
                    );
        }
    }

    private static CarConfiguration 차량(
            CarType carType,
            Engine engine,
            BrakeSystem brake,
            SteeringSystem steering
    ) {
        CarConfiguration car =
                new CarConfiguration();

        car.selectCarType(
                carType
        );

        car.selectEngine(
                engine
        );

        car.selectBrakeSystem(
                brake
        );

        car.selectSteeringSystem(
                steering
        );

        return car;
    }

    private static String 원본_실패사유(
            CarType carType,
            Engine engine,
            BrakeSystem brake,
            SteeringSystem steering
    ) {
        if (
                carType == CarType.SEDAN
                        && brake == BrakeSystem.CONTINENTAL
        ) {
            return "Sedan에는 Continental제동장치 사용 불가";
        }

        if (
                carType == CarType.SUV
                        && engine == Engine.TOYOTA
        ) {
            return "SUV에는 TOYOTA엔진 사용 불가";
        }

        if (
                carType == CarType.TRUCK
                        && engine == Engine.WIA
        ) {
            return "Truck에는 WIA엔진 사용 불가";
        }

        if (
                carType == CarType.TRUCK
                        && brake == BrakeSystem.MANDO
        ) {
            return "Truck에는 Mando제동장치 사용 불가";
        }

        if (
                brake == BrakeSystem.BOSCH
                        && steering != SteeringSystem.BOSCH
        ) {
            return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";
        }

        return null;
    }

    private static Session 실행(
            String commands
    ) throws Exception {

        ByteArrayOutputStream bytes =
                new ByteArrayOutputStream();

        List<Integer> delays =
                new ArrayList<>();

        try (
                Scanner scanner =
                        new Scanner(commands);

                PrintStream output =
                        new PrintStream(
                                bytes,
                                true,
                                StandardCharsets.UTF_8
                        )
        ) {
            ConsoleView view =
                    new ConsoleView(
                            scanner,
                            output
                    );

            CarService service =
                    new CarService(
                            new CompatibilityPolicy()
                    );

            new AssemblyProgram(
                    view,
                    service,
                    delays::add
            ).run();
        }

        return new Session(
                bytes.toString(
                                StandardCharsets.UTF_8
                        )
                        .replace(
                                "\r\n",
                                "\n"
                        ),

                delays
        );
    }

    private static Capture 캡처(
            String input
    ) throws Exception {

        ByteArrayOutputStream bytes =
                new ByteArrayOutputStream();

        Scanner scanner =
                new Scanner(input);

        PrintStream output =
                new PrintStream(
                        bytes,
                        true,
                        StandardCharsets.UTF_8
                );

        return new Capture(
                new ConsoleView(
                        scanner,
                        output
                ),
                bytes
        );
    }

    private static int 등장횟수(
            String text,
            String token
    ) {
        return (
                text.length()
                        - text.replace(
                        token,
                        ""
                ).length()
        ) / token.length();
    }

    private record Session(
            String output,
            List<Integer> delays
    ) {
    }

    private record Capture(
            ConsoleView view,
            ByteArrayOutputStream bytes
    ) {

        String output() {
            return bytes.toString(
                            StandardCharsets.UTF_8
                    )
                    .replace(
                            "\r\n",
                            "\n"
                    );
        }
    }
}