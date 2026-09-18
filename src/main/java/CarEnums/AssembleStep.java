package CarEnums;

import CarAssembly.CarBuilder;

import java.util.function.BiFunction;

public enum AssembleStep {

    CAR_TYPE(
            new String[]{
                    "        ______________",
                    "       /|            |",
                    "  ____/_|_____________|____",
                    " |                      O  |",
                    " '-(@)----------------(@)--'",
                    "===============================",
                    "어떤 차량 타입을 선택할까요?",
                    "1. Sedan",
                    "2. SUV",
                    "3. Truck",
                    "==============================="
            },
            1, 3, "차량 타입은 1 ~ 3 범위만 선택 가능",
            "차량 타입으로 %s을 선택하셨습니다.",
            (answer, builder) -> {
                CarType carType = CarType.values()[answer - 1];
                builder.carType(carType);
                return carType.getLabel();
            }
    ),
    ENGINE(
            new String[]{
                    "어떤 엔진을 탑재할까요?",
                    "0. 뒤로가기",
                    "1. GM",
                    "2. TOYOTA",
                    "3. WIA",
                    "4. 고장난 엔진",
                    "==============================="
            },
            1, 4, "엔진은 1 ~ 4 범위만 선택 가능",
            "%s 엔진을 선택하셨습니다.",
            (answer, builder) -> {
                Engine engine = Engine.values()[answer - 1];
                builder.engine(engine);
                return engine.getLabel();
            }
    ),
    BRAKE_SYSTEM(
            new String[]{
                    "어떤 제동장치를 선택할까요?",
                    "0. 뒤로가기",
                    "1. MANDO",
                    "2. CONTINENTAL",
                    "3. BOSCH",
                    "==============================="
            },
            1, 3, "제동장치는 1 ~ 3 범위만 선택 가능",
            "%s 제동장치를 선택하셨습니다.",
            (answer, builder) -> {
                BrakeSystem brakeSystem = BrakeSystem.values()[answer - 1];
                builder.brakeSystem(brakeSystem);
                return brakeSystem.getLabel();
            }
    ),
    STEERING_SYSTEM(
            new String[]{
                    "어떤 조향장치를 선택할까요?",
                    "0. 뒤로가기",
                    "1. BOSCH",
                    "2. MOBIS",
                    "==============================="
            },
            1, 2, "조향장치는 1 ~ 2 범위만 선택 가능",
            "%s 조향장치를 선택하셨습니다.",
            (answer, builder) -> {
                SteeringSystem steeringSystem = SteeringSystem.values()[answer - 1];
                builder.steeringSystem(steeringSystem);
                return steeringSystem.getLabel();
            }
    );

    private final String[] menuLines;
    private final int minAnswer;
    private final int maxAnswer;
    private final String rangeErrorMessage;
    private final String selectedMessageFormat;
    private final BiFunction<Integer, CarBuilder, String> selector;

    AssembleStep(String[] menuLines, int minAnswer, int maxAnswer, String rangeErrorMessage,
                 String selectedMessageFormat, BiFunction<Integer, CarBuilder, String> selector) {
        this.menuLines = menuLines;
        this.minAnswer = minAnswer;
        this.maxAnswer = maxAnswer;
        this.rangeErrorMessage = rangeErrorMessage;
        this.selectedMessageFormat = selectedMessageFormat;
        this.selector = selector;
    }

    public String[] menuLines() {
        return menuLines;
    }

    public boolean isBackAnswer(int answer) {
        return answer == 0;
    }

    public boolean isInSelectableRange(int answer) {
        return answer >= minAnswer && answer <= maxAnswer;
    }

    public String rangeErrorMessage() {
        return rangeErrorMessage;
    }

    public boolean canGoBack() {
        return ordinal() > 0;
    }

    public AssembleStep previous() {
        return values()[ordinal() - 1];
    }

    public AssembleStep next() {
        AssembleStep[] steps = values();
        int nextOrdinal = ordinal() + 1;
        return nextOrdinal < steps.length ? steps[nextOrdinal] : null;
    }

    public String select(int answer, CarBuilder builder) {
        String label = selector.apply(answer, builder);
        return String.format(selectedMessageFormat, label);
    }
}