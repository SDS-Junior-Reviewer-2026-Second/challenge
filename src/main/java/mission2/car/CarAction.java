package mission2.car;

public enum CarAction {

    RUN("RUN"),
    TEST("Test");

    private final String label;

    CarAction(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

