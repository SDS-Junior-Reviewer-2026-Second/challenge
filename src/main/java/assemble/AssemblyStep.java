package assemble;

import assemble.ui.BrakeSystemUI;
import assemble.ui.CarTypeUI;
import assemble.ui.EngineUI;
import assemble.ui.PartSelectionStep;
import assemble.ui.SteeringSystemUI;
import car.Car;

public enum AssemblyStep {
    CAR_TYPE(new CarTypeUI(), BackNavigation.PREVIOUS),
    ENGINE(new EngineUI(), BackNavigation.PREVIOUS),
    BRAKE_SYSTEM(new BrakeSystemUI(), BackNavigation.PREVIOUS),
    STEERING_SYSTEM(new SteeringSystemUI(), BackNavigation.PREVIOUS),
    RUN_TEST(null, BackNavigation.FIRST) {
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
            return input >= BACK && input <= TEST;
        }

        @Override
        public void select(Car car, int input) {
            throw new UnsupportedOperationException("RUN_TEST 단계에서는 부품을 선택할 수 없습니다.");
        }

        @Override
        public String validationError() {
            return "ERROR :: Run 또는 Test 중 하나를 선택 필요";
        }
    };

    public static final int BACK = 0;
    public static final int RUN = 1;
    public static final int TEST = 2;

    private final PartSelectionStep partUI;
    private final BackNavigation backNavigation;

    AssemblyStep(PartSelectionStep partUI, BackNavigation backNavigation) {
        this.partUI = partUI;
        this.backNavigation = backNavigation;
    }

    public void showMenu() {
        partUI.showMenu();
    }

    public boolean isValidInput(int input) {
        return partUI.isValidInput(input);
    }

    public void select(Car car, int input) {
        partUI.applySelection(car, input);
    }

    public String validationError() {
        return partUI.validationError();
    }

    public BackNavigation getBackNavigation() {
        return backNavigation;
    }

    public boolean isRunTestStep() {
        return this == RUN_TEST;
    }
}
