/**
 * 원래 GM=1, TOYOTA=2, WIA=3 상수와 "고장난 엔진(4)"이라는 특수 값이
 * selectEngine()/runProducedCar()의 삼항 체인에 따로따로 하드코딩돼 있었다.
 * isBroken()으로 "고장 여부" 판단까지 enum 안에 넣어, 호출부에서
 * "engine == 4" 같은 매직 넘버 비교가 필요 없게 했다.
 */
public enum Engine {
    GM(1, "GM"),
    TOYOTA(2, "TOYOTA"),
    WIA(3, "WIA"),
    BROKEN(4, "고장난 엔진");

    private final int code;
    private final String displayName;

    Engine(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBroken() {
        return this == BROKEN;
    }

    public static int maxCode() {
        return values().length;
    }

    public static Engine fromCode(int code) {
        for (Engine engine : values()) {
            if (engine.code == code) {
                return engine;
            }
        }
        throw new IllegalArgumentException("Unknown Engine code: " + code);
    }
}
