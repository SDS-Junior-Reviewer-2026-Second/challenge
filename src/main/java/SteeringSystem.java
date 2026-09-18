/**
 * BrakeSystem과 마찬가지로, 원본은 메뉴/선택 메시지에서 "BOSCH"(대문자), 실행 결과 요약에서는
 * "Bosch"(타이틀 케이스)로 같은 값을 다르게 표기했다. 이 차이를 없애지 않고 그대로 보존하되,
 * menuName/reportName으로 명시적으로 나눠서 의도된 차이임을 드러냈다.
 */
public enum SteeringSystem {
    BOSCH(1, "BOSCH", "Bosch"),
    MOBIS(2, "MOBIS", "Mobis");

    private final int code;
    private final String menuName;
    private final String reportName;

    SteeringSystem(int code, String menuName, String reportName) {
        this.code = code;
        this.menuName = menuName;
        this.reportName = reportName;
    }

    public int getCode() {
        return code;
    }

    /** 메뉴 목록과 "선택하셨습니다" 확인 메시지에 쓰이는 대문자 표기. */
    public String getMenuName() {
        return menuName;
    }

    /** run()/test() 결과 출력에 쓰이는 타이틀 케이스 표기. */
    public String getReportName() {
        return reportName;
    }

    public static int maxCode() {
        return values().length;
    }

    public static SteeringSystem fromCode(int code) {
        for (SteeringSystem steering : values()) {
            if (steering.code == code) {
                return steering;
            }
        }
        throw new IllegalArgumentException("Unknown SteeringSystem code: " + code);
    }
}
