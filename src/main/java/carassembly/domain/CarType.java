package carassembly.domain;

public enum CarType {
    SEDAN("Sedan"), SUV("SUV"), TRUCK("Truck");

    private final String displayName;

    CarType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
