package mission2.display.step;

import java.util.ArrayList;
import java.util.List;

public abstract class Step {

    public static final int BACK = 0;

    static final String SEPARATOR = "===============================";
    static final String NO_BACK = "";
    static final List<String> NO_BANNER = List.of();

    private final int optionCount;
    private final boolean hasBack;
    private final List<String> lines;

    protected Step(String question, String backLabel, List<String> optionLabels, List<String> banner) {
        this.optionCount = optionLabels.size();
        this.hasBack = !backLabel.isEmpty();
        this.lines = draw(question, backLabel, optionLabels, banner);
    }

    public abstract void select(int code, StepListener listener);

    public abstract String rangeError();

    public List<String> lines() {
        return lines;
    }

    public boolean accepts(int code) {
        return (code == BACK && hasBack) || (code >= 1 && code <= optionCount);
    }

    protected static int indexOf(int code) {
        return code - 1;
    }

    private static List<String> draw(String question, String backLabel,
                                     List<String> optionLabels, List<String> banner) {
        List<String> drawn = new ArrayList<>(banner);
        drawn.add(question);
        if (!backLabel.isEmpty()) {
            drawn.add(BACK + ". " + backLabel);
        }
        for (int i = 0; i < optionLabels.size(); i++) {
            drawn.add((i + 1) + ". " + optionLabels.get(i));
        }
        drawn.add(SEPARATOR);
        return List.copyOf(drawn);
    }
}
