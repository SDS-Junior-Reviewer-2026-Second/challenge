package CarEnums;

public enum SteeringSystem {
    BOSCH("Bosch"),
    MOBIS("Mobis");

    private final String label;

    SteeringSystem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}