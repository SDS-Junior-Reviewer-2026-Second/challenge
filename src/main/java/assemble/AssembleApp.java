package assemble;

import assemble.io.Console;
import assemble.model.BrakeSystem;
import assemble.model.CarSpec;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.Part;
import assemble.model.Parts;
import assemble.model.SteeringSystem;
import assemble.rule.CompatibilityRules;

import java.util.List;

/** 자동차 조립 시뮬레이터의 입력 루프와 상태 전이. 모든 입출력은 Console 을 통해서만 한다. */
public class AssembleApp {

    private static final int CarType_Q      = 0;
    private static final int Engine_Q       = 1;
    private static final int BrakeSystem_Q  = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test       = 4;

    private final Console console;
    private CarSpec spec = CarSpec.empty();

    public AssembleApp(Console console) {
        this.console = console;
    }

    public void run() {
        int step = CarType_Q;

        while (true) {
            console.clear();

            switch (step) {
                case CarType_Q:
                    showCarTypeMenu(); break;
                case Engine_Q:
                    showEngineMenu(); break;
                case BrakeSystem_Q:
                    showBrakeMenu(); break;
                case SteeringSystem_Q:
                    showSteeringMenu(); break;
                case Run_Test:
                    showRunTestMenu(); break;
            }

            console.print("INPUT > ");
            String buf = console.readLine().trim();

            if (buf.equalsIgnoreCase("exit")) {
                console.println("바이바이");
                break;
            }

            int answer;
            try {
                answer = Integer.parseInt(buf);
            } catch (NumberFormatException e) {
                console.println("ERROR :: 숫자만 입력 가능");
                console.delay(800);
                continue;
            }

            if (!isValidRange(step, answer)) {
                console.delay(800);
                continue;
            }

            if (answer == 0) {
                if (step == Run_Test) {
                    step = CarType_Q;
                } else if (step > CarType_Q) {
                    step--;
                }
                continue;
            }

            switch (step) {
                case CarType_Q:
                    selectCarType(answer);
                    console.delay(800);
                    step = Engine_Q;
                    break;
                case Engine_Q:
                    selectEngine(answer);
                    console.delay(800);
                    step = BrakeSystem_Q;
                    break;
                case BrakeSystem_Q:
                    selectBrakeSystem(answer);
                    console.delay(800);
                    step = SteeringSystem_Q;
                    break;
                case SteeringSystem_Q:
                    selectSteeringSystem(answer);
                    console.delay(800);
                    step = Run_Test;
                    break;
                case Run_Test:
                    if (answer == 1) {
                        runProducedCar();
                        console.delay(2000);
                    } else if (answer == 2) {
                        console.println("Test...");
                        console.delay(1500);
                        testProducedCar();
                        console.delay(2000);
                    }
                    break;
            }
        }
    }

    private void showCarTypeMenu() {
        console.println("        ______________");
        console.println("       /|            |");
        console.println("  ____/_|_____________|____");
        console.println(" |                      O  |");
        console.println(" '-(@)----------------(@)--'");
        console.println("===============================");
        console.println("어떤 차량 타입을 선택할까요?");
        printMenuItems(CarType.values());
        console.println("===============================");
    }
    private void showEngineMenu() {
        console.println("어떤 엔진을 탑재할까요?");
        console.println("0. 뒤로가기");
        printMenuItems(Engine.values());
        console.println("===============================");
    }
    private void showBrakeMenu() {
        console.println("어떤 제동장치를 선택할까요?");
        console.println("0. 뒤로가기");
        printMenuItems(BrakeSystem.values());
        console.println("===============================");
    }
    private void showSteeringMenu() {
        console.println("어떤 조향장치를 선택할까요?");
        console.println("0. 뒤로가기");
        printMenuItems(SteeringSystem.values());
        console.println("===============================");
    }
    private void showRunTestMenu() {
        console.println("멋진 차량이 완성되었습니다.");
        console.println("어떤 동작을 할까요?");
        console.println("0. 처음 화면으로 돌아가기");
        console.println("1. RUN");
        console.println("2. Test");
        console.println("===============================");
    }

    private void printMenuItems(Part[] parts) {
        for (Part part : parts) {
            console.println(part.code() + ". " + part.displayName());
        }
    }

    private boolean isValidRange(int step, int ans) {
        switch (step) {
            case CarType_Q:
                if (CarType.fromCode(ans).isEmpty()) {
                    console.println("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
                    return false;
                }
                break;
            case Engine_Q:
                if (ans != 0 && Engine.fromCode(ans).isEmpty()) {
                    console.println("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
                    return false;
                }
                break;
            case BrakeSystem_Q:
                if (ans != 0 && BrakeSystem.fromCode(ans).isEmpty()) {
                    console.println("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
                    return false;
                }
                break;
            case SteeringSystem_Q:
                if (ans != 0 && SteeringSystem.fromCode(ans).isEmpty()) {
                    console.println("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
                    return false;
                }
                break;
            case Run_Test:
                if (ans < 0 || ans > 2) {
                    console.println("ERROR :: Run 또는 Test 중 하나를 선택 필요");
                    return false;
                }
                break;
        }
        return true;
    }

    private void selectCarType(int code) {
        CarType carType = CarType.fromCode(code).orElseThrow();
        spec = spec.withCarType(carType);
        console.println(String.format("차량 타입으로 %s을 선택하셨습니다.", carType.displayName()));
    }
    private void selectEngine(int code) {
        Engine engine = Engine.fromCode(code).orElseThrow();
        spec = spec.withEngine(engine);
        console.println(String.format("%s 엔진을 선택하셨습니다.", engine.displayName()));
    }
    private void selectBrakeSystem(int code) {
        BrakeSystem brake = BrakeSystem.fromCode(code).orElseThrow();
        spec = spec.withBrake(brake);
        console.println(String.format("%s 제동장치를 선택하셨습니다.", brake.displayName()));
    }
    private void selectSteeringSystem(int code) {
        SteeringSystem steering = SteeringSystem.fromCode(code).orElseThrow();
        spec = spec.withSteering(steering);
        console.println(String.format("%s 조향장치를 선택하셨습니다.", steering.displayName()));
    }

    private void runProducedCar() {
        if (!CompatibilityRules.isCompatible(spec)) {
            console.println("자동차가 동작되지 않습니다");
            return;
        }
        if (spec.engine().isBroken()) {
            console.println("엔진이 고장나있습니다.");
            console.println("자동차가 움직이지 않습니다.");
            return;
        }

        console.println(String.format("Car Type : %s", spec.carType().displayName()));
        console.println(String.format("Engine   : %s", spec.engine().displayName()));
        console.println(String.format("Brake    : %s", Parts.capitalized(spec.brake())));
        console.println(String.format("Steering : %s", Parts.capitalized(spec.steering())));
        console.println("자동차가 동작됩니다.");
    }

    private void testProducedCar() {
        List<String> violations = CompatibilityRules.violations(spec);
        if (violations.isEmpty()) {
            console.println("자동차 부품 조합 테스트 결과 : PASS");
        } else {
            fail(violations.get(0));
        }
    }

    private void fail(String reason) {
        console.println("자동차 부품 조합 테스트 결과 : FAIL");
        console.println(reason);
    }
}
