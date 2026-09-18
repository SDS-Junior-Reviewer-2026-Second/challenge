package car;

public enum SteeringSystem {

    BOSCH(1, "Bosch"),
    MOBIS(2, "Mobis");

    private final int number;
    private final String displayName;

    SteeringSystem(int number, String displayName) {
        this.number = number;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SteeringSystem from(int number) {
        for (SteeringSystem steering : values()) {
            if (steering.number == number) {
                return steering;
            }
        }

        throw new IllegalArgumentException("잘못된 조향장치");
    }
}
