package org.example.assemble;

public enum Engine {
    GM(1, "GM"),
    TOYOTA(2, "TOYOTA"),
    WIA(3, "WIA"),
    BROKEN(4, "고장난 엔진");

    private final int choice;
    private final String displayName;

    Engine(int choice, String displayName) {
        this.choice = choice;
        this.displayName = displayName;
    }

    public int choice() {
        return choice;
    }

    public String displayName() {
        return displayName;
    }

    public static Engine fromChoice(int choice) {
        return switch (choice) {
            case 1 -> GM;
            case 2 -> TOYOTA;
            case 3 -> WIA;
            case 4 -> BROKEN;
            default -> throw new IllegalArgumentException("Unknown engine choice: " + choice);
        };
    }
}
