/** 조립 화면의 입력 범위와 뒤로가기 규칙을 정의한다. */
enum AssemblyStep {
    CAR_TYPE(1, CarType.values().length,
            "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능"),
    ENGINE(0, Engine.values().length,
            "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능"),
    BRAKE_SYSTEM(0, BrakeSystem.values().length,
            "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능"),
    STEERING_SYSTEM(0, SteeringSystem.values().length,
            "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능"),
    RUN_TEST(0, 2,
            "ERROR :: Run 또는 Test 중 하나를 선택 필요");

    private final int minAnswer;
    private final int maxAnswer;
    private final String errorMessage;

    AssemblyStep(int minAnswer, int maxAnswer, String errorMessage) {
        this.minAnswer = minAnswer;
        this.maxAnswer = maxAnswer;
        this.errorMessage = errorMessage;
    }

    boolean accepts(int answer) {
        return minAnswer <= answer && answer <= maxAnswer;
    }

    String errorMessage() {
        return errorMessage;
    }

    AssemblyStep backStep() {
        return switch (this) {
            case ENGINE -> CAR_TYPE;
            case BRAKE_SYSTEM -> ENGINE;
            case STEERING_SYSTEM -> BRAKE_SYSTEM;
            case RUN_TEST -> CAR_TYPE;
            case CAR_TYPE -> throw new IllegalStateException("첫 화면에서는 뒤로갈 수 없습니다.");
        };
    }
}
