package assemble.rule;

/** 조립된 차를 RUN 했을 때의 판정. 검사 순서는 CarInspector.run 이 정한다. */
public enum RunResult {
    /** 부품 조합 규칙 위반. */
    INCOMPATIBLE,
    /** 조합은 유효하지만 엔진이 고장남. */
    ENGINE_BROKEN,
    /** 정상 동작. */
    RUNNABLE
}
