enum Engine {
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

    int code() {
        return code;
    }

    String displayName() {
        return displayName;
    }

    static Engine fromCode(int code) {
        for (Engine value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown engine code: " + code);
    }
}
