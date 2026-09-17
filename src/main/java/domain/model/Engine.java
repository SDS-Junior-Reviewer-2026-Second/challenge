package domain.model;

public enum Engine {
    GM(1, "GM"), TOYOTA(2, "TOYOTA"), WIA(3, "WIA"), BROKEN(4, "고장난 엔진");

    private final int code;
    private final String label;

    Engine(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Engine fromCode(int code) {
        for (Engine engine : values()) {
            if (engine.code == code) return engine;
        }
        throw new IllegalArgumentException("Unknown Engine code: " + code);
    }
}
