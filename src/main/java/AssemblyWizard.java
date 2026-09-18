import java.util.Scanner;

/**
 * 원본 main()이 하나의 while(true) 안에서 화면 출력, 입력 파싱, 범위 검증, 상태 전이,
 * 부품 선택/실행/테스트 로직까지 전부 처리했다. 여기서는 루프의 "흐름"(읽고, 검증하고,
 * 적용하고, 다음 단계로 넘어간다)만 남기고, 각 단계별 구체적인 내용은 Step에게 위임한다.
 * int step 변수 대신 Step 타입을 쓰므로 잘못된 단계 값(예: 5, -1)이 애초에 존재할 수 없다.
 */
public final class AssemblyWizard {
    private final Scanner scanner;
    private final Car car = new Car();
    private Step step = Step.CAR_TYPE;

    public AssemblyWizard(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            ConsoleUtil.clearScreen();
            step.showMenu();

            System.out.print("INPUT > ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("바이바이");
                return;
            }

            Integer answer = parseAnswer(input);
            if (answer == null) {
                System.out.println("ERROR :: 숫자만 입력 가능");
                ConsoleUtil.delay(800);
                continue;
            }

            if (!step.isValidAnswer(answer)) {
                System.out.println(step.getRangeErrorMessage());
                ConsoleUtil.delay(800);
                continue;
            }

            if (answer == 0) {
                step = step.previousStep();
                continue;
            }

            step.apply(car, answer);
            if (!step.appliesOwnDelay()) {
                ConsoleUtil.delay(800);
            }
            step = step.nextStep();
        }
    }

    private static Integer parseAnswer(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
