package assemble;

import assemble.flow.RunAction;
import assemble.flow.Step;
import assemble.io.Console;
import assemble.model.BrakeSystem;
import assemble.model.CarSpec;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.MenuOption;
import assemble.model.SteeringSystem;
import assemble.rule.CarInspector;
import assemble.ui.ConsoleView;

import java.util.Optional;
import java.util.function.Function;

/** 자동차 조립 시뮬레이터의 입력 루프와 상태 전이. 출력은 ConsoleView, 판정은 CarInspector 에 맡긴다. */
public class AssembleApp {

    private static final String EXIT_COMMAND = "exit";

    private final ConsoleView view;
    private final CarInspector inspector;
    private CarSpec spec = CarSpec.empty();
    private Step step = Step.CAR_TYPE;

    public AssembleApp(Console console, CarInspector inspector) {
        this.view = new ConsoleView(console);
        this.inspector = inspector;
    }

    public void run() {
        while (true) {
            view.showMenu(step);

            Optional<String> input = view.prompt();
            if (input.isEmpty()) {
                return;
            }
            if (input.get().equalsIgnoreCase(EXIT_COMMAND)) {
                view.showGoodbye();
                return;
            }
            parseChoice(input.get()).ifPresent(this::apply);
        }
    }

    /** 현재 단계에서 고를 수 있는 번호면 그 값을, 아니면 에러를 보여 주고 empty 를 돌려준다. */
    private Optional<Integer> parseChoice(String input) {
        Optional<Integer> number = parseNumber(input);
        if (number.isEmpty()) {
            view.showNotANumber();
            return Optional.empty();
        }
        if (!isValidChoice(step, number.get())) {
            view.showOutOfRange(step);
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

    private static boolean isValidChoice(Step step, int choice) {
        return choice == 0 ? step.allowsBack() : step.hasOption(choice);
    }

    private void apply(int choice) {
        if (choice == 0) {
            step = step.back();
            return;
        }
        switch (step) {
            case CAR_TYPE -> select(CarType.fromCode(choice).orElseThrow(), spec::withCarType);
            case ENGINE -> select(Engine.fromCode(choice).orElseThrow(), spec::withEngine);
            case BRAKE -> select(BrakeSystem.fromCode(choice).orElseThrow(), spec::withBrake);
            case STEERING -> select(SteeringSystem.fromCode(choice).orElseThrow(), spec::withSteering);
            case RUN_TEST -> perform(RunAction.fromCode(choice).orElseThrow());
        }
    }

    /** 부품을 사양에 반영하고 다음 단계로 넘어간다. */
    private <O extends MenuOption> void select(O option, Function<O, CarSpec> updateSpec) {
        spec = updateSpec.apply(option);
        view.showSelected(step, option);
        step = step.next();
    }

    private void perform(RunAction action) {
        switch (action) {
            case RUN -> view.showRunResult(inspector.run(spec), spec);
            case TEST -> {
                view.showTesting();
                view.showTestResult(inspector.violations(spec));
            }
        }
    }
}
