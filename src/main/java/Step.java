import java.util.ArrayList;
import java.util.List;

public class Step {

    public static final int BACK = 0;

    private static final int NO_SLOT = -1;
    private static final String SEPARATOR = "===============================";
    private static final String[] NO_BANNER = {};

    private final String title;
    private final String question;
    private final String backLabel;
    private final int partSlot;
    private final String[] options;
    private final String[] banner;

    private Step(String title, String question, String backLabel, int partSlot,
                 String[] options, String[] banner) {
        this.title = title;
        this.question = question;
        this.backLabel = backLabel;
        this.partSlot = partSlot;
        this.options = options;
        this.banner = banner;
    }

    public static Step first(String title, String question, int partSlot, String... options) {
        return new Step(title, question, null, partSlot, options, NO_BANNER);
    }

    public static Step part(String title, String question, int partSlot, String... options) {
        return new Step(title, question, "뒤로가기", partSlot, options, NO_BANNER);
    }

    public static Step action(String title, String question, String... options) {
        return new Step(title, question, "처음 화면으로 돌아가기", NO_SLOT, options, NO_BANNER);
    }

    public Step withBanner(String... banner) {
        return new Step(title, question, backLabel, partSlot, options, banner);
    }

    public boolean isAction() {
        return partSlot == NO_SLOT;
    }

    public int partSlot() {
        return partSlot;
    }

    public String title() {
        return title;
    }

    public boolean accepts(int code) {
        boolean back = code == BACK && backLabel != null;
        return back || (code >= 1 && code <= options.length);
    }

    public String option(int code) {
        return options[code - 1];
    }

    public String rangeError() {
        return "[%s] 1 ~ %d 사이의 번호만 선택 가능".formatted(title, options.length);
    }

    public List<String> lines() {
        List<String> lines = new ArrayList<>(List.of(banner));
        lines.add(SEPARATOR);
        lines.add(question);
        if (backLabel != null) {
            lines.add(BACK + ". " + backLabel);
        }
        for (int i = 0; i < options.length; i++) {
            lines.add((i + 1) + ". " + options[i]);
        }
        lines.add(SEPARATOR);
        return lines;
    }
}
