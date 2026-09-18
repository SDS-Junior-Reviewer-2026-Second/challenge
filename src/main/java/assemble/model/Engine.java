package assemble.model;

import java.util.Optional;

public enum Engine implements MenuOption {
    GM(1, "GM"),
    TOYOTA(2, "TOYOTA"),
    WIA(3, "WIA"),
    BROKEN(4, "고장난 엔진");

    private final int code;
    private final String displayName;

    Engine(int code, String displayName) {
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

    public boolean isBroken() {
        return this == BROKEN;
    }

    public static Optional<Engine> fromCode(int code) {
        return MenuOptions.fromCode(values(), code);
    }
}
