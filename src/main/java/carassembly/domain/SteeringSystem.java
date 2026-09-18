package carassembly.domain;

public enum SteeringSystem {
    BOSCH("Bosch"), MOBIS("Mobis");

    private final String runName;

    SteeringSystem(String runName) {
        this.runName = runName;
    }

    public String runName() {
        return runName;
    }
}
