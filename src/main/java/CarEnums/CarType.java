package CarEnums;

public enum CarType {
    SEDAN("Sedan"),
    SUV("SUV"),
    TRUCK("Truck");

    private final String label;

    CarType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}