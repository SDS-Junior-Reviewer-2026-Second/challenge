package CarAssembly;

import CarEnums.AssembleStep;

public class AssemblyProgress {

    private CarBuilder carBuilder = new CarBuilder();
    private AssembleStep currentStep = AssembleStep.CAR_TYPE;
    private boolean completed = false;

    public boolean isCompleted() {
        return completed;
    }

    public AssembleStep currentStep() {
        return currentStep;
    }

    public CarBuilder carBuilder() {
        return carBuilder;
    }

    public boolean canGoBack() {
        return currentStep.canGoBack();
    }

    public void goToPreviousStep() {
        currentStep = currentStep.previous();
    }

    public String selectAndAdvance(int answer) {
        String message = currentStep.select(answer, carBuilder);

        AssembleStep next = currentStep.next();
        if (next == null) {
            completed = true;
        } else {
            currentStep = next;
        }
        return message;
    }

    public void reset() {
        carBuilder = new CarBuilder();
        currentStep = AssembleStep.CAR_TYPE;
        completed = false;
    }
}