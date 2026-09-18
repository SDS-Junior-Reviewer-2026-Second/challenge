/**
 * 원래 SEDAN=1, SUV=2, TRUCK=3 같은 int 상수와, 코드를 이름으로 바꾸는
 * "a==1?"Sedan":a==2?"SUV":"Truck"" 삼항 체인이 selectCarType()/runProducedCar() 등
 * 여러 곳에 흩어져 중복돼 있었다. 코드-이름 매핑을 enum 하나로 묶고,
 * fromCode()로 변환 로직을 한 곳에 모았다.
 */
public enum CarType {
    SEDAN(1, "Sedan"),
    SUV(2, "SUV"),
    TRUCK(3, "Truck");

    private final int code;
    private final String displayName;

    CarType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static int maxCode() {
        return values().length;
    }

    public static CarType fromCode(int code) {
        for (CarType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown CarType code: " + code);
    }
}
