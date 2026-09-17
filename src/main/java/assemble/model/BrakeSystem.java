package assemble.model;

import java.util.Optional;

public enum BrakeSystem implements MenuOption {
    MANDO(1, "MANDO"),
    CONTINENTAL(2, "CONTINENTAL"),
    BOSCH(3, "BOSCH");

    private final int code;
    private final String displayName;

    BrakeSystem(int code, String displayName) {
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

    public static Optional<BrakeSystem> fromCode(int code) {
        return MenuOptions.fromCode(values(), code);
    }
}
