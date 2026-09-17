enum BrakeSystem {
    MANDO(1, "MANDO", "Mando"),
    CONTINENTAL(2, "CONTINENTAL", "Continental"),
    BOSCH(3, "BOSCH", "Bosch");

    private final int code;
    private final String selectionName;
    private final String runName;

    BrakeSystem(int code, String selectionName, String runName) {
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

    static BrakeSystem fromCode(int code) {
        for (BrakeSystem value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown brake system code: " + code);
    }
}
