package org.example.assemble;

public enum SteeringSystem {
    BOSCH(1, "BOSCH", "Bosch"),
    MOBIS(2, "MOBIS", "Mobis");

    private final int choice;
    private final String selectionName;
    private final String runName;

    SteeringSystem(int choice, String selectionName, String runName) {
        this.choice = choice;
        this.selectionName = selectionName;
        this.runName = runName;
    }

    public int choice() {
        return choice;
    }

    public String selectionName() {
        return selectionName;
    }

    public String runName() {
        return runName;
    }

    public static SteeringSystem fromChoice(int choice) {
        return switch (choice) {
            case 1 -> BOSCH;
            case 2 -> MOBIS;
            default -> throw new IllegalArgumentException("Unknown steering system choice: " + choice);
        };
    }
}
