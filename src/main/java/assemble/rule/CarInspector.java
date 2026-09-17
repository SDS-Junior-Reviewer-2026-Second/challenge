package assemble.rule;

import assemble.model.CarSpec;

import java.util.List;

/** 조립된 차를 규칙 목록으로 검사한다. RUN 판정과 Test 결과가 모두 여기서 나온다. */
public final class CarInspector {

    private final List<CompatibilityRule> rules;

    public CarInspector(List<CompatibilityRule> rules) {
        this.rules = List.copyOf(rules);
    }

    /** 위반한 규칙의 실패 메시지를 정의 순서대로 반환한다. 비어 있으면 통과. */
    public List<String> violations(CarSpec spec) {
        return rules.stream()
                .filter(rule -> rule.isViolatedBy(spec))
                .map(CompatibilityRule::failMessage)
                .toList();
    }

    public boolean isCompatible(CarSpec spec) {
        return violations(spec).isEmpty();
    }

    /** 조합 위반을 먼저 보고, 그 다음 엔진 고장을 본다. */
    public RunResult run(CarSpec spec) {
        if (!isCompatible(spec)) {
            return RunResult.INCOMPATIBLE;
        }
        if (spec.engine().isBroken()) {
            return RunResult.ENGINE_BROKEN;
        }
        return RunResult.RUNNABLE;
    }
}
