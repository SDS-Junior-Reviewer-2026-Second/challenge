import java.util.Scanner;

/** 프로그램 진입점과 실제 의존성 조립만 담당한다. */
public final class Assemble {
    private Assemble() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ConsoleView view = new ConsoleView(scanner, System.out);
        CarService carService = new CarService(new CompatibilityPolicy());
        AssemblyProgram program = new AssemblyProgram(view, carService, Assemble::sleep);
        program.run();

        scanner.close();
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
            // 리팩토링 과제이므로 원본 동작을 그대로 유지한다.
        }
    }
}
