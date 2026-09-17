enum SteeringSystem {
    BOSCH(1, "BOSCH", "Bosch"),
    MOBIS(2, "MOBIS", "Mobis");

    private final int code;
    private final String selectionName;
    private final String runName;

    SteeringSystem(int code, String selectionName, String runName) {
        this.code = code;
        this.selectionName = selectionName;
        this.runName = runName;
    }

    String selectionName() {
        return selectionName;
    }

    String runName() {
        return runName;
    }

    static SteeringSystem fromCode(int code) {
        for (SteeringSystem value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown steering system code: " + code);
    }
}
