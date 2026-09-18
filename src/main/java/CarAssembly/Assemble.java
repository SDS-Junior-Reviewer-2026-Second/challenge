package CarAssembly;

import CarEnums.AssembleStep;

import java.util.Scanner;

public class Assemble {

    private static final int BACK_TO_START = 0;
    private static final int RUN = 1;
    private static final int TEST = 2;

    private static final int DEFAULT_PAUSE_MS = 800;
    private static final int RUN_RESULT_DISPLAY_MS = 2000;
    private static final int TEST_STARTING_DELAY_MS = 1500;
    private static final int TEST_RESULT_DISPLAY_MS = 2000;

    private static final String[] RUN_TEST_MENU = {
            "멋진 차량이 완성되었습니다.",
            "어떤 동작을 할까요?",
            "0. 처음 화면으로 돌아가기",
            "1. RUN",
            "2. Test",
            "==============================="
    };

    private final ConsoleView view = new ConsoleView();
    private final CarRunner carRunner = new CarRunner(view);
    private final Scanner scanner = new Scanner(System.in);
    private final AssemblyProgress progress = new AssemblyProgress();

    public static void main(String[] args) {
        new Assemble().run();
    }

    public void run() {
        while (true) {
            view.clearScreen();
            showCurrentMenu();

            System.out.print("INPUT > ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                view.printLine("바이바이");
                break;
            }

            Integer answer = parseAnswer(input);
            if (answer == null) {
                pause();
                continue;
            }

            if (progress.isCompleted()) {
                handleRunTestInput(answer);
            } else {
                handleAssembleInput(answer);
            }
        }
        scanner.close();
    }

    private void showCurrentMenu() {
        if (progress.isCompleted()) {
            view.printMenu(RUN_TEST_MENU);
        } else {
            view.printMenu(progress.currentStep().menuLines());
        }
    }

    private Integer parseAnswer(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            view.printError("숫자만 입력 가능");
            return null;
        }
    }

    private void handleAssembleInput(int answer) {
        AssembleStep step = progress.currentStep();

        if (step.isBackAnswer(answer) && progress.canGoBack()) {
            progress.goToPreviousStep();
            return;
        }

        if (!step.isInSelectableRange(answer)) {
            view.printError(step.rangeErrorMessage());
            pause();
            return;
        }

        String message = progress.selectAndAdvance(answer);
        view.printLine(message);
        pause();
    }

    private void handleRunTestInput(int answer) {
        if (answer == BACK_TO_START) {
            progress.reset();
        } else if (answer == RUN) {
            carRunner.run(progress.carBuilder().build());
            pause(RUN_RESULT_DISPLAY_MS);
        } else if (answer == TEST) {
            view.printLine("Test...");
            pause(TEST_STARTING_DELAY_MS);
            carRunner.test(progress.carBuilder().build());
            pause(TEST_RESULT_DISPLAY_MS);
        } else {
            view.printError("Run 또는 Test 중 하나를 선택 필요");
            pause();
        }
    }

    private void pause() {
        pause(DEFAULT_PAUSE_MS);
    }

    private void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}