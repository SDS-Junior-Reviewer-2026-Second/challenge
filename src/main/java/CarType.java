public enum CarType implements SelectablePart {
    SEDAN(1, "Sedan"),
    SUV(2, "SUV"),
    TRUCK(3, "Truck");

    private final int code;
    private final String displayName;

    CarType(int code, String displayName) {
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
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        printOptions();
        System.out.println("===============================");
    }

    private static void printOptions() {
        for (CarType carType : values()) {
            System.out.printf("%d. %s%n", carType.code, carType.displayName);
        }
    }

    public static boolean isValidCode(int code) {
        for (CarType carType : values()) {
            if (carType.code == code) {
                return true;
            }
        }
        return false;
    }

    public static CarType fromCode(int code) {
        for (CarType carType : values()) {
            if (carType.code == code) {
                return carType;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 차량 타입 코드입니다: " + code);
    }
}
