package assemble.flow;

import assemble.model.MenuOption;
import assemble.model.MenuOptions;

import java.util.Optional;

/** 조립 완료 화면에서 고를 수 있는 동작. */
public enum RunAction implements MenuOption {
    RUN(1, "RUN"),
    TEST(2, "Test");

    private final int code;
    private final String displayName;

    RunAction(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    public static Optional<RunAction> fromCode(int code) {
        return MenuOptions.fromCode(values(), code);
    }
}
