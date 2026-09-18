package car;

public enum Engine {

    GM(1, "GM"),
    TOYOTA(2, "TOYOTA"),
    WIA(3, "WIA"),
    BROKEN(4, "고장난 엔진");

    private final int number;
    private final String displayName;

    Engine(int number, String displayName) {
        this.number = number;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBroken() {
        return this == BROKEN;
    }

    public static Engine from(int number) {
        for (Engine engine : values()) {
            if (engine.number == number) {
                return engine;
            }
        }

        throw new IllegalArgumentException("잘못된 엔진");
    }
}