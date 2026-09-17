package assemble.flow;

import assemble.model.BrakeSystem;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.MenuOption;
import assemble.model.MenuOptions;
import assemble.model.SteeringSystem;

/** 조립 화면의 단계와 전이. 각 단계가 고를 수 있는 선택지를 안다. 문구는 ConsoleView 가 가진다. */
public enum Step {
    CAR_TYPE(CarType.values(), false),
    ENGINE(Engine.values(), true),
    BRAKE(BrakeSystem.values(), true),
    STEERING(SteeringSystem.values(), true),
    RUN_TEST(RunAction.values(), true);

    private final MenuOption[] options;
    private final boolean allowsBack;

    Step(MenuOption[] options, boolean allowsBack) {
        this.options = options;
        this.allowsBack = allowsBack;
    }

    public MenuOption[] options() {
        return options;
    }

    /** 0 을 입력해 이전(또는 처음) 화면으로 갈 수 있는지. */
    public boolean allowsBack() {
        return allowsBack;
    }

    public boolean hasOption(int code) {
        return MenuOptions.fromCode(options, code).isPresent();
    }

    public Step next() {
        return this == RUN_TEST ? RUN_TEST : values()[ordinal() + 1];
    }

    /** 0 입력 시 이동할 단계. 마지막 화면에서는 처음으로 돌아간다. */
    public Step back() {
        if (this == RUN_TEST) {
            return CAR_TYPE;
        }
        return values()[Math.max(0, ordinal() - 1)];
    }
}
