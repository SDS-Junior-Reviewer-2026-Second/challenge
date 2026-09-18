package parts;

public enum CarType implements SelectablePart {
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
    public int getCode() {
        return code;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static boolean isValidCode(int code) {
        return SelectablePart.isValidCode(values(), code);
    }

    public static CarType fromCode(int code) {
        return SelectablePart.fromCode(values(), code, "차량 타입");
    }
}
