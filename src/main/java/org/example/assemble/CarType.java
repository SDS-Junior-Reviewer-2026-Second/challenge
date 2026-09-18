package org.example.assemble;

public enum CarType {
    SEDAN(1, "Sedan"),
    SUV(2, "SUV"),
    TRUCK(3, "Truck");

    private final int choice;
    private final String displayName;

    CarType(int choice, String displayName) {
        this.choice = choice;
        this.displayName = displayName;
    }

    public int choice() {
        return choice;
    }

    public String displayName() {
        return displayName;
    }

    public static CarType fromChoice(int choice) {
        return switch (choice) {
            case 1 -> SEDAN;
            case 2 -> SUV;
            case 3 -> TRUCK;
            default -> throw new IllegalArgumentException("Unknown car type choice: " + choice);
        };
    }
}
