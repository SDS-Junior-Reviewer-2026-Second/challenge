package assemble.model;

import java.util.Arrays;
import java.util.Optional;

/** Part enum 공통 유틸. */
public final class Parts {
    private Parts() {}

    public static <P extends Part> Optional<P> fromCode(P[] values, int code) {
        return Arrays.stream(values)
                .filter(p -> p.code() == code)
                .findFirst();
    }

    /** 원본 RUN 출력의 "Mando", "Bosch" 처럼 첫 글자만 대문자인 표기. */
    public static String capitalized(Part part) {
        String name = part.displayName();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
