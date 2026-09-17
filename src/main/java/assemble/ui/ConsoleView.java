package assemble.ui;

import assemble.flow.Step;
import assemble.io.Console;
import assemble.model.CarSpec;
import assemble.model.MenuOption;
import assemble.rule.RunResult;

import java.util.List;
import java.util.Optional;

/** 사용자에게 보이는 모든 문구와 화면 흐름의 템포(delay). AssembleApp 은 이 클래스를 통해서만 출력한다. */
public final class ConsoleView {

    private static final String PROMPT = "INPUT > ";
    private static final String MENU_DIVIDER = "===============================";
    private static final int SELECT_DELAY_MS = 800;
    private static final int ERROR_DELAY_MS = 800;
    private static final int TESTING_DELAY_MS = 1500;
    private static final int RESULT_DELAY_MS = 2000;

    /** 단계별 메뉴 문구. backLabel 이 null 이면 0번 항목을 표시하지 않는다. */
    private record Menu(List<String> headerLines, String question, String backLabel, String rangeError) {}

    private final Console console;

    public ConsoleView(Console console) {
        this.console = console;
    }

    public void showMenu(Step step) {
        Menu menu = menuOf(step);
        console.clear();
        menu.headerLines().forEach(console::println);
        console.println(menu.question());
        if (menu.backLabel() != null) {
            console.println("0. " + menu.backLabel());
        }
        for (MenuOption option : step.options()) {
            console.println(option.code() + ". " + option.displayName());
        }
        console.println(MENU_DIVIDER);
    }

    /** 프롬프트를 띄우고 한 줄을 읽는다. 입력이 끝났으면 empty. */
    public Optional<String> prompt() {
        console.print(PROMPT);
        return console.readLine().map(String::trim);
    }

    public void showGoodbye() {
        console.println("바이바이");
    }

    public void showNotANumber() {
        console.println("ERROR :: 숫자만 입력 가능");
        console.delay(ERROR_DELAY_MS);
    }

    public void showOutOfRange(Step step) {
        console.println(menuOf(step).rangeError());
        console.delay(ERROR_DELAY_MS);
    }

    public void showSelected(Step step, MenuOption option) {
        String name = option.displayName();
        console.println(switch (step) {
            case CAR_TYPE -> String.format("차량 타입으로 %s을 선택하셨습니다.", name);
            case ENGINE -> String.format("%s 엔진을 선택하셨습니다.", name);
            case BRAKE -> String.format("%s 제동장치를 선택하셨습니다.", name);
            case STEERING -> String.format("%s 조향장치를 선택하셨습니다.", name);
            case RUN_TEST -> throw new IllegalArgumentException("RUN_TEST 단계에는 부품 선택이 없다");
        });
        console.delay(SELECT_DELAY_MS);
    }

    public void showRunResult(RunResult result, CarSpec spec) {
        switch (result) {
            case INCOMPATIBLE -> console.println("자동차가 동작되지 않습니다");
            case ENGINE_BROKEN -> {
                console.println("엔진이 고장나있습니다.");
                console.println("자동차가 움직이지 않습니다.");
            }
            case RUNNABLE -> showRunningCar(spec);
        }
        console.delay(RESULT_DELAY_MS);
    }

    public void showTesting() {
        console.println("Test...");
        console.delay(TESTING_DELAY_MS);
    }

    /** 위반이 여러 개여도 첫 번째만 보여 준다 (원본 동작). */
    public void showTestResult(List<String> violations) {
        if (violations.isEmpty()) {
            console.println("자동차 부품 조합 테스트 결과 : PASS");
        } else {
            console.println("자동차 부품 조합 테스트 결과 : FAIL");
            console.println(violations.get(0));
        }
        console.delay(RESULT_DELAY_MS);
    }

    private void showRunningCar(CarSpec spec) {
        console.println(String.format("Car Type : %s", spec.carType().displayName()));
        console.println(String.format("Engine   : %s", spec.engine().displayName()));
        console.println(String.format("Brake    : %s", capitalized(spec.brake())));
        console.println(String.format("Steering : %s", capitalized(spec.steering())));
        console.println("자동차가 동작됩니다.");
    }

    /** 원본 RUN 출력의 "Mando", "Bosch" 처럼 첫 글자만 대문자인 표기. */
    private static String capitalized(MenuOption option) {
        String name = option.displayName();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    private static Menu menuOf(Step step) {
        return switch (step) {
            case CAR_TYPE -> new Menu(
                    List.of(
                            "        ______________",
                            "       /|            |",
                            "  ____/_|_____________|____",
                            " |                      O  |",
                            " '-(@)----------------(@)--'",
                            MENU_DIVIDER),
                    "어떤 차량 타입을 선택할까요?",
                    null,
                    "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
            case ENGINE -> new Menu(
                    List.of(),
                    "어떤 엔진을 탑재할까요?",
                    "뒤로가기",
                    "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
            case BRAKE -> new Menu(
                    List.of(),
                    "어떤 제동장치를 선택할까요?",
                    "뒤로가기",
                    "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
            case STEERING -> new Menu(
                    List.of(),
                    "어떤 조향장치를 선택할까요?",
                    "뒤로가기",
                    "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
            case RUN_TEST -> new Menu(
                    List.of("멋진 차량이 완성되었습니다."),
                    "어떤 동작을 할까요?",
                    "처음 화면으로 돌아가기",
                    "ERROR :: Run 또는 Test 중 하나를 선택 필요");
        };
    }
}
