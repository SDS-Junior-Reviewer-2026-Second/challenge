import java.util.Scanner;

/**
 * 원래 이 클래스 하나가 메뉴 출력, 입력 처리, 부품 조합 검증, 실행/테스트까지
 * 전부 담당하는 절차적 코드였다. 각 책임을 CarType/Engine/BrakeSystem/SteeringSystem(부품),
 * Car(도메인 상태), CompatibilityRule(호환성 규칙), Step(화면 상태 기계), AssemblyWizard(루프)로
 * 나누고, 이 클래스는 진입점(main) 역할만 남겼다.
 */
public final class Assemble {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new AssemblyWizard(scanner).run();
        }
    }
}
