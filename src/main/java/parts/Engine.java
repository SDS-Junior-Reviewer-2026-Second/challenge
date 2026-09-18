package parts;

public enum Engine implements SelectablePart {
    GM(1, "GM", false),
    TOYOTA(2, "TOYOTA", false),
    WIA(3, "WIA", false),
    BROKEN(4, "고장난 엔진", true);

    private final int code;
    private final String displayName;
    private final boolean broken;

    Engine(int code, String displayName, boolean broken) {
        this.code = code;
        this.displayName = displayName;
        this.broken = broken;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public boolean isBroken() {
        return broken;
    }

    public static boolean isValidCode(int code) {
        return SelectablePart.isValidCode(values(), code);
    }

    public static Engine fromCode(int code) {
        return SelectablePart.fromCode(values(), code, "엔진");
    }
}
