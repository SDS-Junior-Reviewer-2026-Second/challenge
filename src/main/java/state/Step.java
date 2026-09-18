package state;

public enum Step {
    CAR_TYPE,
    ENGINE,
    BRAKE_SYSTEM,
    STEERING_SYSTEM,
    RUN_TEST;

    public Step next() {
        return switch (this) {
            case CAR_TYPE -> ENGINE;
            case ENGINE -> BRAKE_SYSTEM;
            case BRAKE_SYSTEM -> STEERING_SYSTEM;
            case STEERING_SYSTEM -> RUN_TEST;
            case RUN_TEST -> RUN_TEST;
        };
    }

    public Step prev() {
        return switch (this) {
            case CAR_TYPE -> CAR_TYPE;
            case ENGINE -> CAR_TYPE;
            case BRAKE_SYSTEM -> ENGINE;
            case STEERING_SYSTEM -> BRAKE_SYSTEM;
            case RUN_TEST -> STEERING_SYSTEM;
        };
    }
}