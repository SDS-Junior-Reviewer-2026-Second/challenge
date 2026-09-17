public enum Engine implements SelectablePart {
    GM(1, "GM", false),
    TOYOTA(2, "TOYOTA", false),
    WIA(3, "WIA", false),
    BROKEN(4, "고장난 엔진", true);

    private final int code;
    private final String displayName;
    private final boolean broken;

    Engine(int code, String displayName, boolean broken) {
        this.code = code;
        this.displayName = displayName;
        this.broken = broken;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public boolean isBroken() {
        return broken;
    }

    public static void showMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");
        for (Engine engine : values()) {
            System.out.printf("%d. %s%n", engine.code, engine.displayName);
        }
        System.out.println("===============================");
    }

    public static boolean isValidCode(int code) {
        for (Engine engine : values()) {
            if (engine.code == code) {
                return true;
            }
        }
        return false;
    }

    public static Engine fromCode(int code) {
        for (Engine engine : values()) {
            if (engine.code == code) {
                return engine;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 엔진 코드입니다: " + code);
    }
}
