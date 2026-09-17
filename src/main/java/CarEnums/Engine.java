package CarEnums;

public enum Engine {
    GM("GM"),
    TOYOTA("TOYOTA"),
    WIA("WIA"),
    BROKEN("고장난 엔진");

    private final String label;

    Engine(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isBroken() {
        return this == BROKEN;
    }
}