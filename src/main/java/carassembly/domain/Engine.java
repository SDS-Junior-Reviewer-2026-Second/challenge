package carassembly.domain;

public enum Engine {
    GM, TOYOTA, WIA, BROKEN;

    @Override
    public String toString() {
        return this == BROKEN ? "고장난 엔진" : name();
    }
}
