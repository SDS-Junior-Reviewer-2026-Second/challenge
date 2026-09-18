package mission2.car;

import static mission2.car.BrakeSystemInterface.*;
import static mission2.car.CarTypeInterface.*;
import static mission2.car.EngineInterface.TOYOTA;
import static mission2.car.EngineInterface.WIA;
import static mission2.car.SteeringSystemInterface.BOSCH_S;

public class Car {
    public static final int CarType_Q      = 0;
    public static final int Engine_Q       = 1;
    public static final int BrakeSystem_Q  = 2;
    public static final int SteeringSystem_Q = 3;
    public static final int Run_Test       = 4;

    private int step;
    private final CarType carType = new CarType();
    private final Engine engine = new Engine();
    private final BrakeSystem brakeSystem = new BrakeSystem();
    private final SteeringSystem steeringSystem = new SteeringSystem();

    public int getStep() {
        return step;
    }

    public void executeStep(Integer answer) {
        // precondition check
        if (!isValidRange(answer)) {    // 입력값 범위 체크
            delay(800);
        }else if (answer == 0) {    // 이전 단계 요청시
            if (this.step == Run_Test) {
                this.step = CarType_Q;
            } else if (this.step > CarType_Q) {
                this.step--;
            }
        } else if (step != Run_Test) { // 조립 스탭시
            SelectOptionInterface selectCommend = getOptions();
            if(selectCommend != null) selectCommend.selectOptions(answer);
            delay(800);
            step++;
        } else {    // 실행, 테스트 스탭 시
            if (answer == 1) {
                runProducedCar();
                delay(2000);
            } else if (answer == 2) {
                System.out.println("Test...");
                delay(1500);
                testProducedCar();
                delay(2000);
            }
        }

    }

    public void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    public void fail(String msg) {
        System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
        System.out.println(msg);
    }

    public boolean isValidCheck() {
        if ( carType.getCarType() == SEDAN && brakeSystem.getBrakeSystem() == CONTINENTAL) return false;
        if ( carType.getCarType() == SUV   && engine.getEngine() == TOYOTA)       return false;
        if ( carType.getCarType() == TRUCK && engine.getEngine() == WIA)          return false;
        if ( carType.getCarType() == TRUCK && brakeSystem.getBrakeSystem() == MANDO)  return false;
        if (brakeSystem.getBrakeSystem() == BOSCH_B && steeringSystem.getSteeringSystem() != BOSCH_S) return false;
        return true;
    }

    private void testProducedCar() {
        if (carType.getCarType() == SEDAN && brakeSystem.getBrakeSystem() == CONTINENTAL) {
            fail("Sedan에는 Continental제동장치 사용 불가");
        } else if (carType.getCarType() ==  SUV && engine.getEngine() ==  TOYOTA) {
            fail("SUV에는 TOYOTA엔진 사용 불가");
        } else if (carType.getCarType() ==  TRUCK && engine.getEngine() ==  WIA) {
            fail("Truck에는 WIA엔진 사용 불가");
        } else if (carType.getCarType() ==  TRUCK && brakeSystem.getBrakeSystem() ==  MANDO) {
            fail("Truck에는 Mando제동장치 사용 불가");
        } else if (brakeSystem.getBrakeSystem() ==  BOSCH_B && steeringSystem.getSteeringSystem() !=  BOSCH_S) {
            fail("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
        } else {
            System.out.println("자동차 부품 조합 테스트 결과 : PASS");
        }
    }

    public void runProducedCar() {
        if (! isValidCheck()) {
            System.out.println("자동차가 동작되지 않습니다");
        }
        else if (engine.getEngine() == 4) {
            System.out.println("엔진이 고장나있습니다.");
            System.out.println("자동차가 움직이지 않습니다.");
        }else{
            System.out.printf("Car Type : %s\n", carType.getCarTypeName());
            System.out.printf("Engine   : %s\n", engine.getEngineName());
            System.out.printf("Brake    : %s\n", brakeSystem.getBrakeSystemName());
            System.out.printf("Steering : %s\n", steeringSystem.getSteeringSystemName());
            System.out.println("자동차가 동작됩니다.");
        }
    }

    private SelectOptionInterface getOptions(){
        if (step == CarType_Q) {
            return carType;
        } else if (step == Engine_Q) {
            return engine;
        } else if (step == BrakeSystem_Q) {
            return brakeSystem;
        } else if (step == SteeringSystem_Q) {
            return steeringSystem;
        }
        return null;
    }

    public boolean isValidRange(int ans) {
        SelectOptionInterface selectCommend = getOptions();
        if (selectCommend != null) {
            return selectCommend.validCheck(ans);
        } else if (ans < 0 || ans > 2) {
            System.out.println("ERROR :: Run 또는 Test 중 하나를 선택 필요");
            return false;
        } else {
            return true;
        }
    }
}
