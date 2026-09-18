import java.util.Optional;

/**
 * 원래 int[] stack 배열(인덱스로 CarType_Q, Engine_Q 등을 흉내내던 구조)이
 * 담당하던 "현재 조립 상태"를 대체한다. 배열 인덱스 대신 이름이 있는 필드로
 * 부품을 담기 때문에 stack[BrakeSystem_Q] 같은 코드를 읽을 필요가 없다.
 *
 * run()/test()는 각각 원본의 runProducedCar()/testProducedCar()를 옮긴 것인데,
 * 두 메서드가 완전히 같은 호환성 규칙 5개를 서로 다른 형태(불리언 vs 메시지 출력)로
 * 중복 구현하던 것을 CompatibilityRule.firstViolation() 하나로 공유하도록 합쳤다.
 * 다만 "엔진 고장" 체크는 run()에만 있고 test()에는 없는 원본의 비대칭 동작은 그대로 뒀다
 * (테스트는 부품 "조합"만 검증하고, 엔진이 실제로 고장났는지는 검증 대상이 아니었음).
 */
public final class Car {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public BrakeSystem getBrakeSystem() {
        return brakeSystem;
    }

    public void setBrakeSystem(BrakeSystem brakeSystem) {
        this.brakeSystem = brakeSystem;
    }

    public SteeringSystem getSteeringSystem() {
        return steeringSystem;
    }

    public void setSteeringSystem(SteeringSystem steeringSystem) {
        this.steeringSystem = steeringSystem;
    }

    public void run() {
        Optional<CompatibilityRule> violation = CompatibilityRule.firstViolation(this);
        if (violation.isPresent()) {
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
        System.out.printf("Brake    : %s%n", brakeSystem.getReportName());
        System.out.printf("Steering : %s%n", steeringSystem.getReportName());
        System.out.println("자동차가 동작됩니다.");
    }

    public void test() {
        Optional<CompatibilityRule> violation = CompatibilityRule.firstViolation(this);
        if (violation.isPresent()) {
            System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
            System.out.println(violation.get().getMessage());
        } else {
            System.out.println("자동차 부품 조합 테스트 결과 : PASS");
        }
    }
}
