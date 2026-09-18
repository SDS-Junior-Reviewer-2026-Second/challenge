import java.util.function.BiConsumer;

/**
 * 원본 main()에는 "지금 어느 화면인가"를 기준으로 나뉘는 switch문이 3개
 * (메뉴 출력용, isValidRange()의 범위 검증용, 답변 처리용) 평행하게 존재했다.
 * 새 케이스(화면)를 추가하려면 세 군데를 모두 고쳐야 하는 구조였다.
 * 화면 하나(CAR_TYPE, ENGINE, BRAKE, STEERING, RUN_TEST)를 enum 상수 하나로 만들고
 * 메뉴 출력/입력 범위/답변 처리 로직을 전부 그 상수 안에 담아서,
 * AssemblyWizard는 switch 없이 step.xxx() 호출만으로 동작하게 했다(상태 패턴).
 */
public enum Step {
    CAR_TYPE(Step::showCarTypeMenu, 1, CarType.maxCode(),
            "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능", false,
            (car, answer) -> {
                CarType type = CarType.fromCode(answer);
                car.setCarType(type);
                System.out.printf("차량 타입으로 %s을 선택하셨습니다.%n", type.getDisplayName());
            }),
    ENGINE(Step::showEngineMenu, 0, Engine.maxCode(),
            "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능", false,
            (car, answer) -> {
                Engine engine = Engine.fromCode(answer);
                car.setEngine(engine);
                System.out.printf("%s 엔진을 선택하셨습니다.%n", engine.getDisplayName());
            }),
    BRAKE(Step::showBrakeMenu, 0, BrakeSystem.maxCode(),
            "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능", false,
            (car, answer) -> {
                BrakeSystem brake = BrakeSystem.fromCode(answer);
                car.setBrakeSystem(brake);
                System.out.printf("%s 제동장치를 선택하셨습니다.%n", brake.getMenuName());
            }),
    STEERING(Step::showSteeringMenu, 0, SteeringSystem.maxCode(),
            "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능", false,
            (car, answer) -> {
                SteeringSystem steering = SteeringSystem.fromCode(answer);
                car.setSteeringSystem(steering);
                System.out.printf("%s 조향장치를 선택하셨습니다.%n", steering.getMenuName());
            }),
    RUN_TEST(Step::showRunTestMenu, 0, 2,
            "ERROR :: Run 또는 Test 중 하나를 선택 필요", true,
            (car, answer) -> {
                if (answer == 1) {
                    car.run();
                    ConsoleUtil.delay(2000);
                } else if (answer == 2) {
                    System.out.println("Test...");
                    ConsoleUtil.delay(1500);
                    car.test();
                    ConsoleUtil.delay(2000);
                }
            });

    private final Runnable menuPrinter;
    private final int minAnswer;
    private final int maxAnswer;
    private final String rangeErrorMessage;
    private final boolean appliesOwnDelay;
    private final BiConsumer<Car, Integer> handler;

    Step(Runnable menuPrinter, int minAnswer, int maxAnswer, String rangeErrorMessage,
         boolean appliesOwnDelay, BiConsumer<Car, Integer> handler) {
        this.menuPrinter = menuPrinter;
        this.minAnswer = minAnswer;
        this.maxAnswer = maxAnswer;
        this.rangeErrorMessage = rangeErrorMessage;
        this.appliesOwnDelay = appliesOwnDelay;
        this.handler = handler;
    }

    public void showMenu() {
        menuPrinter.run();
    }

    public boolean isValidAnswer(int answer) {
        return answer >= minAnswer && answer <= maxAnswer;
    }

    public String getRangeErrorMessage() {
        return rangeErrorMessage;
    }

    public void apply(Car car, int answer) {
        handler.accept(car, answer);
    }

    /**
     * RUN_TEST는 답변(1=RUN, 2=Test)에 따라 지연 시간이 다르고 중간에 "Test..." 출력까지
     * 끼어 있어서, 다른 4단계처럼 "선택 후 무조건 800ms"로 처리할 수 없다.
     * 처음에는 AssemblyWizard가 "if (step != Step.RUN_TEST)"로 이 예외를 직접 알고 있었는데,
     * 그러면 오케스트레이터가 특정 enum 값에 의존하게 되어 상태 패턴의 의미가 깨진다.
     * 대신 이 플래그를 두어 "이 단계가 자기 지연을 스스로 책임지는가"만 물어보게 했다.
     */
    public boolean appliesOwnDelay() {
        return appliesOwnDelay;
    }

    public Step nextStep() {
        Step[] steps = values();
        return steps[Math.min(ordinal() + 1, steps.length - 1)];
    }

    /**
     * 원본 main()의 "if (step == Run_Test) step = CarType_Q; else if (...) step--;" 분기와
     * 동일하게, RUN_TEST에서 뒤로가기(0)는 한 단계 전(STEERING)이 아니라 처음 화면으로
     * 완전히 재시작한다. 다른 4단계는 단순히 이전 enum 상수로 한 칸 이동한다.
     */
    public Step previousStep() {
        if (this == RUN_TEST) {
            return CAR_TYPE;
        }
        return values()[Math.max(ordinal() - 1, 0)];
    }

    // 원본은 "1. MANDO", "2. CONTINENTAL" 같은 메뉴 줄을 println으로 직접 하드코딩했는데,
    // 이 문자열은 BrakeSystem 등 enum이 이미 갖고 있는 code/menuName과 내용이 겹치는
    // 중복이었다. 아래 메뉴들은 println 대신 enum values()를 순회해서 만들기 때문에,
    // enum 쪽에 상수를 추가/변경하면 메뉴도 자동으로 맞춰진다.
    private static void showCarTypeMenu() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        for (CarType type : CarType.values()) {
            System.out.printf("%d. %s%n", type.getCode(), type.getDisplayName());
        }
        System.out.println("===============================");
    }

    private static void showEngineMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");
        for (Engine engine : Engine.values()) {
            System.out.printf("%d. %s%n", engine.getCode(), engine.getDisplayName());
        }
        System.out.println("===============================");
    }

    private static void showBrakeMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        for (BrakeSystem brake : BrakeSystem.values()) {
            System.out.printf("%d. %s%n", brake.getCode(), brake.getMenuName());
        }
        System.out.println("===============================");
    }

    private static void showSteeringMenu() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        for (SteeringSystem steering : SteeringSystem.values()) {
            System.out.printf("%d. %s%n", steering.getCode(), steering.getMenuName());
        }
        System.out.println("===============================");
    }

    private static void showRunTestMenu() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
        System.out.println("===============================");
    }
}
