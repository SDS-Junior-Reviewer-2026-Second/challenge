package assemble;

import parts.BrakeSystem;
import parts.Car;
import parts.CarType;
import parts.Engine;
import parts.SelectablePart;
import parts.SteeringSystem;

public enum AssemblyStep {
    CAR_TYPE(BackNavigation.PREVIOUS) {
        @Override
        public void showMenu() {
            CarType.showMenu();
        }

        @Override
        public boolean isValidInput(int input) {
            return CarType.isValidCode(input);
        }

        @Override
        public void select(Car car, int input) {
            car.selectCarType(input);
            System.out.printf("차량 타입으로 %s을 선택하셨습니다.%n", car.getCarType().getDisplayName());
        }

        @Override
        public String validationError() {
            return "ERROR :: 차량 타입은 " + SelectablePart.codeRange(CarType.values()) + " 범위만 선택 가능";
        }
    },
    ENGINE(BackNavigation.PREVIOUS) {
        @Override
        public void showMenu() {
            Engine.showMenu();
        }

        @Override
        public boolean isValidInput(int input) {
            return input == BACK || Engine.isValidCode(input);
        }

        @Override
        public void select(Car car, int input) {
            car.selectEngine(input);
            System.out.printf("%s 엔진을 선택하셨습니다.%n", car.getEngine().getDisplayName());
        }

        @Override
        public String validationError() {
            return "ERROR :: 엔진은 " + SelectablePart.codeRange(Engine.values()) + " 범위만 선택 가능";
        }
    },
    BRAKE_SYSTEM(BackNavigation.PREVIOUS) {
        @Override
        public void showMenu() {
            BrakeSystem.showMenu();
        }

        @Override
        public boolean isValidInput(int input) {
            return input == BACK || BrakeSystem.isValidCode(input);
        }

        @Override
        public void select(Car car, int input) {
            car.selectBrakeSystem(input);
            System.out.printf("%s 제동장치를 선택하셨습니다.%n", car.getBrakeSystem().getDisplayName());
        }

        @Override
        public String validationError() {
            return "ERROR :: 제동장치는 " + SelectablePart.codeRange(BrakeSystem.values()) + " 범위만 선택 가능";
        }
    },
    STEERING_SYSTEM(BackNavigation.PREVIOUS) {
        @Override
        public void showMenu() {
            SteeringSystem.showMenu();
        }

        @Override
        public boolean isValidInput(int input) {
            return input == BACK || SteeringSystem.isValidCode(input);
        }

        @Override
        public void select(Car car, int input) {
            car.selectSteeringSystem(input);
            System.out.printf("%s 조향장치를 선택하셨습니다.%n", car.getSteeringSystem().getDisplayName());
        }

        @Override
        public String validationError() {
            return "ERROR :: 조향장치는 " + SelectablePart.codeRange(SteeringSystem.values()) + " 범위만 선택 가능";
        }
    },
    RUN_TEST(BackNavigation.FIRST) {
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

    private final BackNavigation backNavigation;

    AssemblyStep(BackNavigation backNavigation) {
        this.backNavigation = backNavigation;
    }

    public abstract void showMenu();

    public abstract boolean isValidInput(int input);

    public abstract void select(Car car, int input);

    public abstract String validationError();

    public BackNavigation getBackNavigation() {
        return backNavigation;
    }

    public boolean isRunTestStep() {
        return this == RUN_TEST;
    }
}
