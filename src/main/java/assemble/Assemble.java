package assemble;

import assemble.model.BrakeSystem;
import assemble.model.CarSpec;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.Part;
import assemble.model.Parts;
import assemble.model.SteeringSystem;
import assemble.rule.CompatibilityRules;

import java.util.List;
import java.util.Scanner;

public class Assemble {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private static final int CarType_Q      = 0;
    private static final int Engine_Q       = 1;
    private static final int BrakeSystem_Q  = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test       = 4;

    private static CarSpec spec = CarSpec.empty();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int step = CarType_Q;

        while (true) {
            System.out.print(CLEAR_SCREEN);
            System.out.flush();

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

            System.out.print("INPUT > ");
            String buf = sc.nextLine().trim();

            if (buf.equalsIgnoreCase("exit")) {
                System.out.println("바이바이");
                break;
            }

            int answer;
            try {
                answer = Integer.parseInt(buf);
            } catch (NumberFormatException e) {
                System.out.println("ERROR :: 숫자만 입력 가능");
                delay(800);
                continue;
            }

            if (!isValidRange(step, answer)) {
                delay(800);
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
                    delay(800);
                    step = Engine_Q;
                    break;
                case Engine_Q:
                    selectEngine(answer);
                    delay(800);
                    step = BrakeSystem_Q;
                    break;
                case BrakeSystem_Q:
                    selectBrakeSystem(answer);
                    delay(800);
                    step = SteeringSystem_Q;
                    break;
                case SteeringSystem_Q:
                    selectSteeringSystem(answer);
                    delay(800);
                    step = Run_Test;
                    break;
                case Run_Test:
                    if (answer == 1) {
                        runProducedCar();
                        delay(2000);
                    } else if (answer == 2) {
                        System.out.println("Test...");
                        delay(1500);
                        testProducedCar();
                        delay(2000);
                    }
                    break;
            }
        }

        sc.close();
    }

    private static void showCarTypeMenu() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        printMenuItems(CarType.values());
        System.out.println("===============================");
    }
    private static void showEngineMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");
        printMenuItems(Engine.values());
        System.out.println("===============================");
    }
    private static void showBrakeMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        printMenuItems(BrakeSystem.values());
        System.out.println("===============================");
    }
    private static void showSteeringMenu() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        printMenuItems(SteeringSystem.values());
        System.out.println("===============================");
    }
    private static void showRunTestMenu() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
        System.out.println("===============================");
    }

    private static void printMenuItems(Part[] parts) {
        for (Part part : parts) {
            System.out.println(part.code() + ". " + part.displayName());
        }
    }

    private static boolean isValidRange(int step, int ans) {
        switch (step) {
            case CarType_Q:
                if (CarType.fromCode(ans).isEmpty()) {
                    System.out.println("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
                    return false;
                }
                break;
            case Engine_Q:
                if (ans != 0 && Engine.fromCode(ans).isEmpty()) {
                    System.out.println("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
                    return false;
                }
                break;
            case BrakeSystem_Q:
                if (ans != 0 && BrakeSystem.fromCode(ans).isEmpty()) {
                    System.out.println("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
                    return false;
                }
                break;
            case SteeringSystem_Q:
                if (ans != 0 && SteeringSystem.fromCode(ans).isEmpty()) {
                    System.out.println("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
                    return false;
                }
                break;
            case Run_Test:
                if (ans < 0 || ans > 2) {
                    System.out.println("ERROR :: Run 또는 Test 중 하나를 선택 필요");
                    return false;
                }
                break;
        }
        return true;
    }

    private static void selectCarType(int code) {
        CarType carType = CarType.fromCode(code).orElseThrow();
        spec = spec.withCarType(carType);
        System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n", carType.displayName());
    }
    private static void selectEngine(int code) {
        Engine engine = Engine.fromCode(code).orElseThrow();
        spec = spec.withEngine(engine);
        System.out.printf("%s 엔진을 선택하셨습니다.\n", engine.displayName());
    }
    private static void selectBrakeSystem(int code) {
        BrakeSystem brake = BrakeSystem.fromCode(code).orElseThrow();
        spec = spec.withBrake(brake);
        System.out.printf("%s 제동장치를 선택하셨습니다.\n", brake.displayName());
    }
    private static void selectSteeringSystem(int code) {
        SteeringSystem steering = SteeringSystem.fromCode(code).orElseThrow();
        spec = spec.withSteering(steering);
        System.out.printf("%s 조향장치를 선택하셨습니다.\n", steering.displayName());
    }


    private static void runProducedCar() {
        if (!CompatibilityRules.isCompatible(spec)) {
            System.out.println("자동차가 동작되지 않습니다");
            return;
        }
        if (spec.engine().isBroken()) {
            System.out.println("엔진이 고장나있습니다.");
            System.out.println("자동차가 움직이지 않습니다.");
            return;
        }

        System.out.printf("Car Type : %s\n", spec.carType().displayName());
        System.out.printf("Engine   : %s\n", spec.engine().displayName());
        System.out.printf("Brake    : %s\n", Parts.capitalized(spec.brake()));
        System.out.printf("Steering : %s\n", Parts.capitalized(spec.steering()));
        System.out.println("자동차가 동작됩니다.");
    }

    private static void testProducedCar() {
        List<String> violations = CompatibilityRules.violations(spec);
        if (violations.isEmpty()) {
            System.out.println("자동차 부품 조합 테스트 결과 : PASS");
        } else {
            fail(violations.get(0));
        }
    }

    private static void fail(String msg) {
        System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
        System.out.println(msg);
    }


    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
