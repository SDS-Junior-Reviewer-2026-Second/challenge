package car;

public enum CarType {

    SEDAN(1, "Sedan"),
    SUV(2, "SUV"),
    TRUCK(3, "Truck");

    private final int number;
    private final String displayName;

    CarType(int number, String displayName) {
        this.number = number;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CarType from(int number) {
        for (CarType type : values()) {
            if (type.number == number) {
                return type;
            }
        }

        throw new IllegalArgumentException("잘못된 차량 타입");
    }
}