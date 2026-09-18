package assemble;

import java.util.List;

public class AssemblyFlow {
    private static final List<AssemblyStep> STEPS = List.of(
            AssemblyStep.CAR_TYPE,
            AssemblyStep.ENGINE,
            AssemblyStep.BRAKE_SYSTEM,
            AssemblyStep.STEERING_SYSTEM,
            AssemblyStep.RUN_TEST
    );

    private int currentIndex;

    public AssemblyStep currentStep() {
        return STEPS.get(currentIndex);
    }

    public void moveNext() {
        if (currentIndex < STEPS.size() - 1) {
            currentIndex++;
        }
    }

    public void moveBack() {
        currentIndex = currentStep().getBackNavigation().targetIndex(currentIndex);
    }
}
