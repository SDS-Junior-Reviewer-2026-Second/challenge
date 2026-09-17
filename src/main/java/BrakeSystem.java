public enum BrakeSystem implements SelectablePart {
    MANDO(1, "Mando"),
    CONTINENTAL(2, "Continental"),
    BOSCH(3, "Bosch");

    private final int code;
    private final String displayName;

    BrakeSystem(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static void showMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        for (BrakeSystem brakeSystem : values()) {
            System.out.printf("%d. %s%n", brakeSystem.code, brakeSystem.name());
        }
        System.out.println("===============================");
    }

    public static boolean isValidCode(int code) {
        for (BrakeSystem brakeSystem : values()) {
            if (brakeSystem.code == code) {
                return true;
            }
        }
        return false;
    }

    public static BrakeSystem fromCode(int code) {
        for (BrakeSystem brakeSystem : values()) {
            if (brakeSystem.code == code) {
                return brakeSystem;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 제동장치 코드입니다: " + code);
    }
}
