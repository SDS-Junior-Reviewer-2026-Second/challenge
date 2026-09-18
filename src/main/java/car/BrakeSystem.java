package car;

public enum BrakeSystem {

    MANDO(1, "Mando"),
    CONTINENTAL(2, "Continental"),
    BOSCH(3, "Bosch");

    private final int number;
    private final String displayName;

    BrakeSystem(int number, String displayName) {
        this.number = number;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BrakeSystem from(int number) {
        for (BrakeSystem brake : values()) {
            if (brake.number == number) {
                return brake;
            }
        }

        throw new IllegalArgumentException("잘못된 제동장치");
    }
}