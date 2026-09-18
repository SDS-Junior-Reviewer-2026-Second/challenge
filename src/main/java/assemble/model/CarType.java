package assemble.model;

import java.util.Optional;

public enum CarType implements MenuOption {
    SEDAN(1, "Sedan"),
    SUV(2, "SUV"),
    TRUCK(3, "Truck");

    private final int code;
    private final String displayName;

    CarType(int code, String displayName) {
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

    public static Optional<CarType> fromCode(int code) {
        return MenuOptions.fromCode(values(), code);
    }
}
