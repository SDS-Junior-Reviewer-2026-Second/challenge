package domain.model;

public enum SteeringSystem {
    BOSCH(1, "Bosch"), MOBIS(2, "Mobis");

    private final int code;
    private final String label;

    SteeringSystem(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static SteeringSystem fromCode(int code) {
        for (SteeringSystem steering : values()) {
            if (steering.code == code) return steering;
        }
        throw new IllegalArgumentException("Unknown SteeringSystem code: " + code);
    }
}
