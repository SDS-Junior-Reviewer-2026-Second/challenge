package carassembly.domain;

public enum BrakeSystem {
    MANDO("Mando"), CONTINENTAL("Continental"), BOSCH("Bosch");

    private final String runName;

    BrakeSystem(String runName) {
        this.runName = runName;
    }

    // 선택 화면의 대문자 이름과 주행 결과의 이름이 서로 다른 원본 동작을 유지한다.
    public String runName() {
        return runName;
    }
}
