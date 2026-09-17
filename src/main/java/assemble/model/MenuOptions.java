package assemble.model;

import java.util.Arrays;
import java.util.Optional;

/** MenuOption enum 공통 유틸. */
public final class MenuOptions {
    private MenuOptions() {}

    public static <O extends MenuOption> Optional<O> fromCode(O[] values, int code) {
        return Arrays.stream(values)
                .filter(option -> option.code() == code)
                .findFirst();
    }
}
