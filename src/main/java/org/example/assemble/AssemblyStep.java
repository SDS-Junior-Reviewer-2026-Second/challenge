package org.example.assemble;

public enum AssemblyStep {
    CAR_TYPE(1, 3, "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능"),
    ENGINE(0, 4, "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능"),
    BRAKE_SYSTEM(0, 3, "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능"),
    STEERING_SYSTEM(0, 2, "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능"),
    RUN_TEST(0, 2, "ERROR :: Run 또는 Test 중 하나를 선택 필요");

    private final int minimumChoice;
    private final int maximumChoice;
    private final String rangeErrorMessage;

    AssemblyStep(int minimumChoice, int maximumChoice, String rangeErrorMessage) {
        this.minimumChoice = minimumChoice;
        this.maximumChoice = maximumChoice;
        this.rangeErrorMessage = rangeErrorMessage;
    }

    public boolean accepts(int choice) {
        return choice >= minimumChoice && choice <= maximumChoice;
    }

    public String rangeErrorMessage() {
        return rangeErrorMessage;
    }

    public AssemblyStep previous() {
        return switch (this) {
            case CAR_TYPE -> CAR_TYPE;
            case ENGINE -> CAR_TYPE;
            case BRAKE_SYSTEM -> ENGINE;
            case STEERING_SYSTEM -> BRAKE_SYSTEM;
            case RUN_TEST -> CAR_TYPE;
        };
    }
}
