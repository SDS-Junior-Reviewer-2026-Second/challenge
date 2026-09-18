package mission2.display.step;

import mission2.car.CarAction;

import java.util.List;

public class ActionStep extends Step {

    private static final List<CarAction> ACTIONS = List.of(CarAction.values());

    ActionStep() {
        super("어떤 동작을 할까요?", "처음 화면으로 돌아가기",
                ACTIONS.stream().map(CarAction::label).toList(),
                List.of("멋진 차량이 완성되었습니다."));
    }

    @Override
    public void select(int code, StepListener listener) {
        listener.actionSelected(ACTIONS.get(indexOf(code)));
    }

    @Override
    public String rangeError() {
        return "Run 또는 Test 중 하나를 선택 필요";
    }
}
