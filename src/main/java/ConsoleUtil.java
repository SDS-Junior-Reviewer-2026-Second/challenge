/**
 * 원래 Assemble 클래스 안에 CLEAR_SCREEN 상수와 delay() 메서드가 private static으로
 * 박혀 있어서 다른 클래스(Step, Car 등)에서 재사용할 수 없었다. 화면 초기화/지연 같은
 * "콘솔 제어" 관심사만 따로 떼어내 여러 클래스가 공유하도록 했다.
 */
public final class ConsoleUtil {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private ConsoleUtil() {}

    public static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}
