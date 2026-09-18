package assemble.ui;

import assemble.AssemblyStep;
import car.Car;

public class RunTestUI implements AssemblyStepUI {
    private final CarUI carUI = new CarUI();

    @Override
    public void showMenu() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
        System.out.println("===============================");
    }

    @Override
    public boolean isValidInput(int input) {
        return input >= AssemblyStep.BACK && input <= AssemblyStep.TEST;
    }

    @Override
    public String validationError() {
        return "ERROR :: Run 또는 Test 중 하나를 선택 필요";
    }

    @Override
    public StepAction execute(Car car, int input) {
        if (input == AssemblyStep.RUN) {
            carUI.runProducedCar(car);
            delay(2000);
        } else if (input == AssemblyStep.TEST) {
            System.out.println("Test...");
            delay(1500);
            carUI.testProducedCar(car);
            delay(2000);
        }
        return StepAction.STAY;
    }

    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
        }
    }
}
