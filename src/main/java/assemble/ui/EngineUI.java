package assemble.ui;

import assemble.AssemblyStep;
import car.Car;
import parts.Engine;
import parts.SelectablePart;

public class EngineUI implements PartUI {
    @Override
    public void showMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");
        for (Engine engine : Engine.values()) {
            System.out.printf("%d. %s%n", engine.getCode(), engine.getDisplayName());
        }
        System.out.println("===============================");
    }

    @Override
    public boolean isValidInput(int input) {
        return input == AssemblyStep.BACK || Engine.isValidCode(input);
    }

    @Override
    public void applySelection(Car car, int input) {
        car.selectEngine(input);
        System.out.printf("%s 엔진을 선택하셨습니다.%n", car.getEngine().getDisplayName());
    }

    @Override
    public String validationError() {
        return "ERROR :: 엔진은 " + SelectablePart.codeRange(Engine.values()) + " 범위만 선택 가능";
    }
}
