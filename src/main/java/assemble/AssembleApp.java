package assemble;

import assemble.flow.RunAction;
import assemble.flow.Step;
import assemble.io.Console;
import assemble.model.BrakeSystem;
import assemble.model.CarSpec;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.MenuOption;
import assemble.model.MenuOptions;
import assemble.model.SteeringSystem;
import assemble.rule.CarInspector;

import java.util.List;
import java.util.Optional;

/** 자동차 조립 시뮬레이터의 입력 루프와 상태 전이. 모든 입출력은 Console 을 통해서만 한다. */
public class AssembleApp {

    private static final String EXIT_COMMAND = "exit";
    private static final String MENU_DIVIDER = "===============================";
    private static final int SELECT_DELAY_MS = 800;
    private static final int ERROR_DELAY_MS = 800;
    private static final int TESTING_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    private final Console console;
    private final CarInspector inspector;
    private CarSpec spec = CarSpec.empty();
    private Step step = Step.CAR_TYPE;

    public AssembleApp(Console console, CarInspector inspector) {
        this.console = console;
        this.inspector = inspector;
    }

    public void run() {
        while (true) {
            console.clear();
            showMenu();

            Optional<String> input = prompt();
            if (input.isEmpty()) {
                return;
            }
            if (input.get().equalsIgnoreCase(EXIT_COMMAND)) {
                console.println("바이바이");
                return;
            }
            parseChoice(input.get()).ifPresent(this::apply);
        }
    }

    /** 입력 프롬프트를 띄우고 한 줄을 읽는다. 입력이 끝났으면 empty. */
    private Optional<String> prompt() {
        console.print("INPUT > ");
        return console.readLine().map(String::trim);
    }

    /** 현재 단계에서 고를 수 있는 번호면 그 값을, 아니면 에러를 출력하고 empty 를 돌려준다. */
    private Optional<Integer> parseChoice(String input) {
        Optional<Integer> number = parseNumber(input);
        Optional<String> error = number.isEmpty()
                ? Optional.of("ERROR :: 숫자만 입력 가능")
                : validationError(step, number.get());
        if (error.isPresent()) {
            console.println(error.get());
            console.delay(ERROR_DELAY_MS);
            return Optional.empty();
        }
        return number;
    }

    private static Optional<Integer> parseNumber(String input) {
        try {
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<String> validationError(Step step, int choice) {
        boolean valid = choice == 0 ? step.allowsBack() : step.hasOption(choice);
        return valid ? Optional.empty() : Optional.of(step.rangeError());
    }

    private void apply(int choice) {
        if (choice == 0) {
            step = step.back();
            return;
        }
        handle(choice);
    }

    private void showMenu() {
        step.headerLines().forEach(console::println);
        console.println(step.question());
        if (step.allowsBack()) {
            console.println("0. " + step.backLabel());
        }
        for (MenuOption option : step.options()) {
            console.println(option.code() + ". " + option.displayName());
        }
        console.println(MENU_DIVIDER);
    }

    private void handle(int choice) {
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
        switch (inspector.run(spec)) {
            case INCOMPATIBLE -> console.println("자동차가 동작되지 않습니다");
            case ENGINE_BROKEN -> {
                console.println("엔진이 고장나있습니다.");
                console.println("자동차가 움직이지 않습니다.");
            }
            case RUNNABLE -> printRunningCar();
        }
    }

    private void printRunningCar() {
        console.println(String.format("Car Type : %s", spec.carType().displayName()));
        console.println(String.format("Engine   : %s", spec.engine().displayName()));
        console.println(String.format("Brake    : %s", MenuOptions.capitalized(spec.brake())));
        console.println(String.format("Steering : %s", MenuOptions.capitalized(spec.steering())));
        console.println("자동차가 동작됩니다.");
    }

    private void testProducedCar() {
        List<String> violations = inspector.violations(spec);
        if (violations.isEmpty()) {
            console.println("자동차 부품 조합 테스트 결과 : PASS");
        } else {
            console.println("자동차 부품 조합 테스트 결과 : FAIL");
            console.println(violations.get(0));
        }
    }
}
