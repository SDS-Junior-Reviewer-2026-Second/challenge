package domain.model;

public enum BrakeSystem {
    MANDO(1, "Mando"), CONTINENTAL(2, "Continental"), BOSCH(3, "Bosch");

    private final int code;
    private final String label;

    BrakeSystem(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BrakeSystem fromCode(int code) {
        for (BrakeSystem brake : values()) {
            if (brake.code == code) return brake;
        }
        throw new IllegalArgumentException("Unknown BrakeSystem code: " + code);
    }
}
