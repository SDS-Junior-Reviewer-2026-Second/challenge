package assemble;

import assemble.flow.RunAction;
import assemble.flow.Step;
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
import java.util.Optional;

/** 자동차 조립 시뮬레이터의 입력 루프와 상태 전이. 모든 입출력은 Console 을 통해서만 한다. */
public class AssembleApp {

    private static final String MENU_DIVIDER = "===============================";
    private static final int SELECT_DELAY_MS = 800;
    private static final int ERROR_DELAY_MS = 800;
    private static final int TESTING_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    private final Console console;
    private CarSpec spec = CarSpec.empty();
    private Step step = Step.CAR_TYPE;

    public AssembleApp(Console console) {
        this.console = console;
    }

    public void run() {
        while (true) {
            console.clear();
            showMenu(step);

            console.print("INPUT > ");
            Optional<String> line = console.readLine();
            if (line.isEmpty()) {
                break;
            }
            String input = line.get().trim();

            if (input.equalsIgnoreCase("exit")) {
                console.println("바이바이");
                break;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                console.println("ERROR :: 숫자만 입력 가능");
                console.delay(ERROR_DELAY_MS);
                continue;
            }

            if (!isValidChoice(step, choice)) {
                console.delay(ERROR_DELAY_MS);
                continue;
            }

            if (choice == 0) {
                step = step.back();
                continue;
            }

            handle(step, choice);
        }
    }

    private void showMenu(Step step) {
        step.headerLines().forEach(console::println);
        console.println(step.question());
        if (step.allowsBack()) {
            console.println("0. " + step.backLabel());
        }
        for (Part option : step.options()) {
            console.println(option.code() + ". " + option.displayName());
        }
        console.println(MENU_DIVIDER);
    }

    private boolean isValidChoice(Step step, int choice) {
        boolean valid = choice == 0 ? step.allowsBack() : step.hasOption(choice);
        if (!valid) {
            console.println(step.rangeError());
        }
        return valid;
    }

    private void handle(Step step, int choice) {
        switch (step) {
            case CAR_TYPE -> selectPart(CarType.fromCode(choice).orElseThrow());
            case ENGINE -> selectPart(Engine.fromCode(choice).orElseThrow());
            case BRAKE -> selectPart(BrakeSystem.fromCode(choice).orElseThrow());
            case STEERING -> selectPart(SteeringSystem.fromCode(choice).orElseThrow());
            case RUN_TEST -> perform(RunAction.fromCode(choice).orElseThrow());
        }
    }

    private void selectPart(CarType carType) {
        spec = spec.withCarType(carType);
        advance(String.format("차량 타입으로 %s을 선택하셨습니다.", carType.displayName()));
    }

    private void selectPart(Engine engine) {
        spec = spec.withEngine(engine);
        advance(String.format("%s 엔진을 선택하셨습니다.", engine.displayName()));
    }

    private void selectPart(BrakeSystem brake) {
        spec = spec.withBrake(brake);
        advance(String.format("%s 제동장치를 선택하셨습니다.", brake.displayName()));
    }

    private void selectPart(SteeringSystem steering) {
        spec = spec.withSteering(steering);
        advance(String.format("%s 조향장치를 선택하셨습니다.", steering.displayName()));
    }

    private void advance(String selectionMessage) {
        console.println(selectionMessage);
        console.delay(SELECT_DELAY_MS);
        step = step.next();
    }

    private void perform(RunAction action) {
        switch (action) {
            case RUN -> {
                runProducedCar();
                console.delay(RESULT_DELAY_MS);
            }
            case TEST -> {
                console.println("Test...");
                console.delay(TESTING_DELAY_MS);
                testProducedCar();
                console.delay(RESULT_DELAY_MS);
            }
        }
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
            console.println("자동차 부품 조합 테스트 결과 : FAIL");
            console.println(violations.get(0));
        }
    }
}
