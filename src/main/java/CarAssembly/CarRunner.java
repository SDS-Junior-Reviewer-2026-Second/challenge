package CarAssembly;

import java.util.Optional;

public class CarRunner {

    private final CarValidator validator = new CarValidator();
    private final ConsoleView view;

    public CarRunner(ConsoleView view) {
        this.view = view;
    }

    public void run(Car car) {
        Optional<String> violation = validator.findFirstViolation(car);
        if (violation.isPresent()) {
            view.printLine("자동차가 동작되지 않습니다");
            return;
        }
        if (car.hasBrokenEngine()) {
            view.printLine("엔진이 고장나있습니다.");
            view.printLine("자동차가 움직이지 않습니다.");
            return;
        }
        view.printLine(car.describe());
        view.printLine("자동차가 동작됩니다.");
    }

    public void test(Car car) {
        Optional<String> violation = validator.findFirstViolation(car);

        if (violation.isPresent()) {
            view.printLine("자동차 부품 조합 테스트 결과 : FAIL");
            view.printLine(violation.get());
        } else {
            view.printLine("자동차 부품 조합 테스트 결과 : PASS");
        }
    }
}