public enum SteeringSystem implements SelectablePart {
    BOSCH(1, "Bosch"),
    MOBIS(2, "Mobis");

    private final int code;
    private final String displayName;

    SteeringSystem(int code, String displayName) {
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
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        for (SteeringSystem steeringSystem : values()) {
            System.out.printf("%d. %s%n", steeringSystem.code, steeringSystem.name());
        }
        System.out.println("===============================");
    }

    public static boolean isValidCode(int code) {
        for (SteeringSystem steeringSystem : values()) {
            if (steeringSystem.code == code) {
                return true;
            }
        }
        return false;
    }

    public static SteeringSystem fromCode(int code) {
        for (SteeringSystem steeringSystem : values()) {
            if (steeringSystem.code == code) {
                return steeringSystem;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 조향장치 코드입니다: " + code);
    }
}
