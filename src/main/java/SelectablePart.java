public interface SelectablePart {
    int getCode();

    String getDisplayName();

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
