package assemble.ui;

import assemble.AssemblyStep;
import car.Car;
import parts.SelectablePart;
import parts.SteeringSystem;

public class SteeringSystemUI implements PartSelectionStep {
    @Override
    public void showMenu() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        for (SteeringSystem steeringSystem : SteeringSystem.values()) {
            System.out.printf("%d. %s%n", steeringSystem.getCode(), steeringSystem.name());
        }
        System.out.println("===============================");
    }

    @Override
    public boolean isValidInput(int input) {
        return input == AssemblyStep.BACK || SteeringSystem.isValidCode(input);
    }

    @Override
    public void applySelection(Car car, int input) {
        car.selectSteeringSystem(input);
        System.out.printf("%s 조향장치를 선택하셨습니다.%n", car.getSteeringSystem().getDisplayName());
    }

    @Override
    public String validationError() {
        return "ERROR :: 조향장치는 " + SelectablePart.codeRange(SteeringSystem.values()) + " 범위만 선택 가능";
    }
}
