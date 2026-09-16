import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.IntConsumer;

public class Assemble {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final String SEPARATOR = "===============================";
    private static final int SELECTION_DELAY_MS = 800;
    private static final int TEST_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    private final Scanner input;
    private final PrintStream output;
    private final IntConsumer pause;

    private Step step = Step.CAR_TYPE;
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    // 입력, 출력, 대기를 전달받아 실제 콘솔이나 대기 시간 없이도 검증할 수 있다.
    Assemble(Scanner input, PrintStream output, IntConsumer pause) {
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
        this.pause = Objects.requireNonNull(pause);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new Assemble(scanner, System.out, Assemble::delay).run();
        }
    }

    void run() {
        while (!Thread.currentThread().isInterrupted()) {
            showMenu();
            output.print("INPUT > ");
            if (!input.hasNextLine()) {
                return;
            }

            String command = input.nextLine().trim();
            if (command.equalsIgnoreCase("exit")) {
                output.println("바이바이");
                return;
            }

            Integer answer = parseAnswer(command);
            if (answer == null) {
                continue;
            }

            if (answer == 0) {
                step = step.previous();
            } else if (step == Step.RUN_TEST) {
                executeAction(answer);
            } else {
                selectComponent(answer);
                pause.accept(SELECTION_DELAY_MS);
                step = step.next();
            }
        }
    }

    private Integer parseAnswer(String command) {
        final int answer;
        try {
            answer = Integer.parseInt(command);
        } catch (NumberFormatException e) {
            showInputError("ERROR :: 숫자만 입력 가능");
            return null;
        }

        if (!step.accepts(answer)) {
            showInputError(step.errorMessage);
            return null;
        }
        return answer;
    }

    private void showInputError(String message) {
        output.println(message);
        pause.accept(SELECTION_DELAY_MS);
    }

    private void showMenu() {
        output.print(CLEAR_SCREEN);
        output.flush();

        if (step == Step.CAR_TYPE) {
            output.println("        ______________");
            output.println("       /|            |");
            output.println("  ____/_|_____________|____");
            output.println(" |                      O  |");
            output.println(" '-(@)----------------(@)--'");
            output.println(SEPARATOR);
        } else if (step == Step.RUN_TEST) {
            output.println("멋진 차량이 완성되었습니다.");
        }

        output.println(step.question);
        if (step != Step.CAR_TYPE) {
            output.println(step == Step.RUN_TEST
                    ? "0. 처음 화면으로 돌아가기"
                    : "0. 뒤로가기");
        }
        for (int i = 0; i < step.options.length; i++) {
            output.printf("%d. %s%n", i + 1, step.options[i]);
        }
        output.println(SEPARATOR);
    }

    private void selectComponent(int answer) {
        // 메뉴도 같은 enum 선언 순서로 생성하므로 선택 번호와 부품이 일치한다.
        int index = answer - 1;
        switch (step) {
            case CAR_TYPE -> {
                carType = CarType.values()[index];
                output.printf("차량 타입으로 %s을 선택하셨습니다.\n", carType);
            }
            case ENGINE -> {
                engine = Engine.values()[index];
                output.printf("%s 엔진을 선택하셨습니다.\n", engine);
            }
            case BRAKE_SYSTEM -> {
                brakeSystem = BrakeSystem.values()[index];
                output.printf("%s 제동장치를 선택하셨습니다.\n", brakeSystem);
            }
            case STEERING_SYSTEM -> {
                steeringSystem = SteeringSystem.values()[index];
                output.printf("%s 조향장치를 선택하셨습니다.\n", steeringSystem);
            }
            case RUN_TEST -> throw new IllegalStateException("부품 선택 단계가 아닙니다.");
        }
    }

    private void executeAction(int answer) {
        Car car = new Car(carType, engine, brakeSystem, steeringSystem);
        if (answer == 1) {
            runProducedCar(car);
        } else {
            output.println("Test...");
            pause.accept(TEST_DELAY_MS);
            testProducedCar(car);
        }
        pause.accept(RESULT_DELAY_MS);
    }

    private void runProducedCar(Car car) {
        if (car.findCompatibilityError().isPresent()) {
            output.println("자동차가 동작되지 않습니다");
            return;
        }
        if (car.hasBrokenEngine()) {
            output.println("엔진이 고장나있습니다.");
            output.println("자동차가 움직이지 않습니다.");
            return;
        }

        output.printf("Car Type : %s\n", car.carType());
        output.printf("Engine   : %s\n", car.engine());
        output.printf("Brake    : %s\n", car.brakeSystem().runName);
        output.printf("Steering : %s\n", car.steeringSystem().runName);
        output.println("자동차가 동작됩니다.");
    }

    private void testProducedCar(Car car) {
        Optional<String> error = car.findCompatibilityError();
        if (error.isPresent()) {
            output.println("자동차 부품 조합 테스트 결과 : FAIL");
            output.println(error.get());
        } else {
            output.println("자동차 부품 조합 테스트 결과 : PASS");
        }
    }

    private static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    enum Step {
        CAR_TYPE("어떤 차량 타입을 선택할까요?", 1,
                "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능", labels(CarType.values())),
        ENGINE("어떤 엔진을 탑재할까요?", 0,
                "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능", labels(Engine.values())),
        BRAKE_SYSTEM("어떤 제동장치를 선택할까요?", 0,
                "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능", labels(BrakeSystem.values())),
        STEERING_SYSTEM("어떤 조향장치를 선택할까요?", 0,
                "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능", labels(SteeringSystem.values())),
        RUN_TEST("어떤 동작을 할까요?", 0,
                "ERROR :: Run 또는 Test 중 하나를 선택 필요", "RUN", "Test");

        private final String question;
        private final int minimumAnswer;
        private final String errorMessage;
        private final String[] options;

        Step(String question, int minimumAnswer, String errorMessage, String... options) {
            this.question = question;
            this.minimumAnswer = minimumAnswer;
            this.errorMessage = errorMessage;
            this.options = options;
        }

        boolean accepts(int answer) {
            return answer >= minimumAnswer && answer <= options.length;
        }

        Step next() {
            return switch (this) {
                case CAR_TYPE -> ENGINE;
                case ENGINE -> BRAKE_SYSTEM;
                case BRAKE_SYSTEM -> STEERING_SYSTEM;
                case STEERING_SYSTEM, RUN_TEST -> RUN_TEST;
            };
        }

        Step previous() {
            return switch (this) {
                case CAR_TYPE, ENGINE, RUN_TEST -> CAR_TYPE;
                case BRAKE_SYSTEM -> ENGINE;
                case STEERING_SYSTEM -> BRAKE_SYSTEM;
            };
        }

        private static String[] labels(Enum<?>[] values) {
            return Arrays.stream(values).map(Object::toString).toArray(String[]::new);
        }
    }

    enum CarType {
        SEDAN("Sedan"), SUV("SUV"), TRUCK("Truck");

        private final String displayName;

        CarType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    enum Engine {
        GM, TOYOTA, WIA, BROKEN;

        @Override
        public String toString() {
            return this == BROKEN ? "고장난 엔진" : name();
        }
    }

    enum BrakeSystem {
        MANDO("Mando"), CONTINENTAL("Continental"), BOSCH("Bosch");

        private final String runName;

        BrakeSystem(String runName) {
            this.runName = runName;
        }
    }

    enum SteeringSystem {
        BOSCH("Bosch"), MOBIS("Mobis");

        private final String runName;

        SteeringSystem(String runName) {
            this.runName = runName;
        }
    }

    // 조립된 차량의 데이터와 규칙만 다루며 콘솔에 직접 출력하지 않는다.
    record Car(CarType carType, Engine engine,
               BrakeSystem brakeSystem, SteeringSystem steeringSystem) {
        Car {
            Objects.requireNonNull(carType);
            Objects.requireNonNull(engine);
            Objects.requireNonNull(brakeSystem);
            Objects.requireNonNull(steeringSystem);
        }

        // 기존 순서를 유지해 여러 문제가 있어도 첫 번째 원인만 반환한다.
        Optional<String> findCompatibilityError() {
            if (carType == CarType.SEDAN && brakeSystem == BrakeSystem.CONTINENTAL) {
                return Optional.of("Sedan에는 Continental제동장치 사용 불가");
            }
            if (carType == CarType.SUV && engine == Engine.TOYOTA) {
                return Optional.of("SUV에는 TOYOTA엔진 사용 불가");
            }
            if (carType == CarType.TRUCK && engine == Engine.WIA) {
                return Optional.of("Truck에는 WIA엔진 사용 불가");
            }
            if (carType == CarType.TRUCK && brakeSystem == BrakeSystem.MANDO) {
                return Optional.of("Truck에는 Mando제동장치 사용 불가");
            }
            if (brakeSystem == BrakeSystem.BOSCH && steeringSystem != SteeringSystem.BOSCH) {
                return Optional.of("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
            }
            return Optional.empty();
        }

        boolean hasBrokenEngine() {
            return engine == Engine.BROKEN;
        }
    }
}
