package assemble.ui;

import assemble.AssemblyStep;
import parts.BrakeSystem;
import car.Car;
import parts.SelectablePart;

public class BrakeSystemUI implements PartUI {
    @Override
    public void showMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        for (BrakeSystem brakeSystem : BrakeSystem.values()) {
            System.out.printf("%d. %s%n", brakeSystem.getCode(), brakeSystem.name());
        }
        System.out.println("===============================");
    }

    @Override
    public boolean isValidInput(int input) {
        return input == AssemblyStep.BACK || BrakeSystem.isValidCode(input);
    }

    @Override
    public void applySelection(Car car, int input) {
        car.selectBrakeSystem(input);
        System.out.printf("%s 제동장치를 선택하셨습니다.%n", car.getBrakeSystem().getDisplayName());
    }

    @Override
    public String validationError() {
        return "ERROR :: 제동장치는 " + SelectablePart.codeRange(BrakeSystem.values()) + " 범위만 선택 가능";
    }
}
