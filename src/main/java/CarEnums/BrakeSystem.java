package CarEnums;

public enum BrakeSystem {
    MANDO("Mando"),
    CONTINENTAL("Continental"),
    BOSCH("Bosch");

    private final String label;

    BrakeSystem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}