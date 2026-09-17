package assemble.model;

import java.util.Optional;

public enum SteeringSystem implements Part {
    BOSCH(1, "BOSCH"),
    MOBIS(2, "MOBIS");

    private final int code;
    private final String displayName;

    SteeringSystem(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    public static Optional<SteeringSystem> fromCode(int code) {
        return Parts.fromCode(values(), code);
    }
}
