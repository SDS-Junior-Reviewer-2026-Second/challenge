public class Car {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public void selectCarType(int code) {
        carType = CarType.fromCode(code);
    }

    public void selectEngine(int code) {
        engine = Engine.fromCode(code);
    }

    public void selectBrakeSystem(int code) {
        brakeSystem = BrakeSystem.fromCode(code);
    }

    public void selectSteeringSystem(int code) {steeringSystem = SteeringSystem.fromCode(code);}

    public CarType getCarType() {
        return carType;
    }

    public Engine getEngine() {
        return engine;
    }

    public BrakeSystem getBrakeSystem() {
        return brakeSystem;
    }

    public SteeringSystem getSteeringSystem() {
        return steeringSystem;
    }

    public boolean isValid() {
        return validationError() == null;
    }

    public void runProducedCar() {
        if (!isValid()) {
            System.out.println("자동차가 동작되지 않습니다");
            return;
        }
        if (engine.isBroken()) {
            System.out.println("엔진이 고장나있습니다.");
            System.out.println("자동차가 움직이지 않습니다.");
            return;
        }

        System.out.printf("Car Type : %s%n", carType.getDisplayName());
        System.out.printf("Engine   : %s%n", engine.getDisplayName());
        System.out.printf("Brake    : %s%n", brakeSystem.getDisplayName());
        System.out.printf("Steering : %s%n", steeringSystem.getDisplayName());
        System.out.println("자동차가 동작됩니다.");
    }

    public void testProducedCar() {
        String error = validationError();
        if (error == null) {
            System.out.println("자동차 부품 조합 테스트 결과 : PASS");
            return;
        }

        System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
        System.out.println(error);
    }

    private String validationError() {
        if (carType == CarType.SEDAN && brakeSystem == BrakeSystem.CONTINENTAL) {
            return "Sedan에는 Continental제동장치 사용 불가";
        }
        if (carType == CarType.SUV && engine == Engine.TOYOTA) {
            return "SUV에는 TOYOTA엔진 사용 불가";
        }
        if (carType == CarType.TRUCK && engine == Engine.WIA) {
            return "Truck에는 WIA엔진 사용 불가";
        }
        if (carType == CarType.TRUCK && brakeSystem == BrakeSystem.MANDO) {
            return "Truck에는 Mando제동장치 사용 불가";
        }
        if (brakeSystem == BrakeSystem.BOSCH && steeringSystem != SteeringSystem.BOSCH) {
            return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";
        }
        return null;
    }
}
