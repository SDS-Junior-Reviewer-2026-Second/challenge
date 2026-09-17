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

    /** 원본 RUN 출력의 "Mando", "Bosch" 처럼 첫 글자만 대문자인 표기. */
    public static String capitalized(MenuOption option) {
        String name = option.displayName();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
