package CarAssembly;

public class ConsoleView {

    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final String ERROR_PREFIX = "ERROR :: ";

    public void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    public void printMenu(String... lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public void printLine(String message) {
        System.out.println(message);
    }

    public void printError(String message) {
        System.out.println(ERROR_PREFIX + message);
    }
}