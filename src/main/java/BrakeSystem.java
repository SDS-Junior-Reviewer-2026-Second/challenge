/**
 * 원본은 메뉴/선택 메시지에서는 "MANDO"처럼 전부 대문자로, 실행 결과 요약(runProducedCar)과
 * 테스트 실패 메시지에서는 "Mando"처럼 타이틀 케이스로 같은 값을 서로 다르게 출력했다.
 * 이 표기 불일치는 리팩토링 대상이 아니라 원본 동작 그대로 보존하기로 했고,
 * 대신 menuName/reportName 두 필드로 명시적으로 분리해 "왜 이름이 두 개인지"가
 * 코드만 봐도 드러나게 했다.
 */
public enum BrakeSystem {
    MANDO(1, "MANDO", "Mando"),
    CONTINENTAL(2, "CONTINENTAL", "Continental"),
    BOSCH(3, "BOSCH", "Bosch");

    private final int code;
    private final String menuName;
    private final String reportName;

    BrakeSystem(int code, String menuName, String reportName) {
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

    public static BrakeSystem fromCode(int code) {
        for (BrakeSystem brake : values()) {
            if (brake.code == code) {
                return brake;
            }
        }
        throw new IllegalArgumentException("Unknown BrakeSystem code: " + code);
    }
}
