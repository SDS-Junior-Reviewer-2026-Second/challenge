package org.example.assemble;

public enum BrakeSystem {
    MANDO(1, "MANDO", "Mando"),
    CONTINENTAL(2, "CONTINENTAL", "Continental"),
    BOSCH(3, "BOSCH", "Bosch");

    private final int choice;
    private final String selectionName;
    private final String runName;

    BrakeSystem(int choice, String selectionName, String runName) {
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

    public static BrakeSystem fromChoice(int choice) {
        return switch (choice) {
            case 1 -> MANDO;
            case 2 -> CONTINENTAL;
            case 3 -> BOSCH;
            default -> throw new IllegalArgumentException("Unknown brake system choice: " + choice);
        };
    }
}
