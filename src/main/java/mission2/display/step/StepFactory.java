package mission2.display.step;

import mission2.car.AssemblyOrder;
import mission2.car.part.PartCategory;

import java.util.ArrayList;
import java.util.List;

public class StepFactory {

    private static final String BACK_LABEL = "뒤로가기";

    private static final List<String> OPENING_BANNER = List.of(
            "        ______________",
            "       /|            |",
            "  ____/_|_____________|____",
            " |                      O  |",
            " '-(@)----------------(@)--'",
            Step.SEPARATOR);

    private StepFactory() {
    }

    public static List<Step> assemblyLine() {
        List<Step> steps = new ArrayList<>();
        for (PartCategory category : AssemblyOrder.categories()) {
            steps.add(steps.isEmpty() ? openingStep(category) : nextStep(category));
        }
        steps.add(new ActionStep());
        return List.copyOf(steps);
    }

    private static PartStep openingStep(PartCategory category) {
        return new PartStep(category, Step.NO_BACK, OPENING_BANNER);
    }

    private static PartStep nextStep(PartCategory category) {
        return new PartStep(category, BACK_LABEL, Step.NO_BANNER);
    }
}
