import carassembly.application.AssemblyController;
import carassembly.console.ConsoleView;

import java.util.Scanner;

/** 실행에 필요한 객체를 연결하는 진입점. */
public class Assemble {
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            ConsoleView view = new ConsoleView(input, System.out);
            new AssemblyController(view, Assemble::delay).run();
        }
    }

    private static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
