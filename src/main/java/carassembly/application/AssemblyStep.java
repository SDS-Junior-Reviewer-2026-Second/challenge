package carassembly.application;

import carassembly.domain.BrakeSystem;
import carassembly.domain.CarType;
import carassembly.domain.Engine;
import carassembly.domain.SteeringSystem;

import java.util.Arrays;
import java.util.List;

/** 단계의 메뉴 정보, 입력 범위, 앞뒤 이동 규칙을 한곳에서 정의한다. */
public enum AssemblyStep {
    CAR_TYPE("어떤 차량 타입을 선택할까요?", 1,
            "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능", labels(CarType.values())),
    ENGINE("어떤 엔진을 탑재할까요?", 0,
            "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능", labels(Engine.values())),
    BRAKE_SYSTEM("어떤 제동장치를 선택할까요?", 0,
            "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능", labels(BrakeSystem.values())),
    STEERING_SYSTEM("어떤 조향장치를 선택할까요?", 0,
            "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능", labels(SteeringSystem.values())),
    RUN_TEST("어떤 동작을 할까요?", 0,
            "ERROR :: Run 또는 Test 중 하나를 선택 필요", List.of("RUN", "Test"));

    private final String question;
    private final int minimumAnswer;
    private final String errorMessage;
    private final List<String> options;

    AssemblyStep(String question, int minimumAnswer, String errorMessage, List<String> options) {
        this.question = question;
        this.minimumAnswer = minimumAnswer;
        this.errorMessage = errorMessage;
        this.options = List.copyOf(options);
    }

    public String question() {
        return question;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public List<String> options() {
        return options;
    }

    public boolean accepts(int answer) {
        return answer >= minimumAnswer && answer <= options.size();
    }

    public AssemblyStep next() {
        return switch (this) {
            case CAR_TYPE -> ENGINE;
            case ENGINE -> BRAKE_SYSTEM;
            case BRAKE_SYSTEM -> STEERING_SYSTEM;
            case STEERING_SYSTEM, RUN_TEST -> RUN_TEST;
        };
    }

    public AssemblyStep previous() {
        return switch (this) {
            case CAR_TYPE, ENGINE, RUN_TEST -> CAR_TYPE;
            case BRAKE_SYSTEM -> ENGINE;
            case STEERING_SYSTEM -> BRAKE_SYSTEM;
        };
    }

    private static List<String> labels(Enum<?>[] values) {
        return Arrays.stream(values).map(Object::toString).toList();
    }
}
