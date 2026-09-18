package assemble;

public enum BackNavigation {
    PREVIOUS {
        @Override
        public int targetIndex(int currentIndex) {
            return Math.max(0, currentIndex - 1);
        }
    },
    FIRST {
        @Override
        public int targetIndex(int currentIndex) {
            return 0;
        }
    };

    public abstract int targetIndex(int currentIndex);
}
