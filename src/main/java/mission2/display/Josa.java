package mission2.display;

public class Josa {

    private static final int HANGUL_FIRST = 0xAC00;
    private static final int FINAL_CONSONANT_COUNT = 28;

    private Josa() {
    }

    public static String topic(String word) {
        return endsWithConsonant(word) ? "은" : "는";
    }

    public static String subject(String word) {
        return endsWithConsonant(word) ? "이" : "가";
    }

    private static boolean endsWithConsonant(String word) {
        return (word.charAt(word.length() - 1) - HANGUL_FIRST) % FINAL_CONSONANT_COUNT != 0;
    }
}
