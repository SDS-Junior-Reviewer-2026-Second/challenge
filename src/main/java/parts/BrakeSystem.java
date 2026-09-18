package parts;

public enum BrakeSystem implements SelectablePart {
    MANDO(1, "Mando"),
    CONTINENTAL(2, "Continental"),
    BOSCH(3, "Bosch");

    private final int code;
    private final String displayName;

    BrakeSystem(int code, String displayName) {
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

    public static BrakeSystem fromCode(int code) {
        return SelectablePart.fromCode(values(), code, "제동장치");
    }
}
