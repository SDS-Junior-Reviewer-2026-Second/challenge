package parts;

public interface SelectablePart {
    int getCode();

    String getDisplayName();

    static <T extends Enum<T> & SelectablePart> boolean isValidCode(T[] parts, int code) {
        for (T part : parts) {
            if (part.getCode() == code) {
                return true;
            }
        }
        return false;
    }

    static <T extends Enum<T> & SelectablePart> T fromCode(T[] parts, int code, String partName) {
        for (T part : parts) {
            if (part.getCode() == code) {
                return part;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 " + partName + " 코드입니다: " + code);
    }

    static String codeRange(SelectablePart[] parts) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (SelectablePart part : parts) {
            min = Math.min(min, part.getCode());
            max = Math.max(max, part.getCode());
        }

        return min + " ~ " + max;
    }
}
