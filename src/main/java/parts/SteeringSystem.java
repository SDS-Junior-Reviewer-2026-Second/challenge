package parts;

public enum SteeringSystem implements SelectablePart {
    BOSCH(1, "Bosch"),
    MOBIS(2, "Mobis");

    private final int code;
    private final String displayName;

    SteeringSystem(int code, String displayName) {
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

    public static SteeringSystem fromCode(int code) {
        return SelectablePart.fromCode(values(), code, "조향장치");
    }
}
