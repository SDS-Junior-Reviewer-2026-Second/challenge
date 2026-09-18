package mission2.display;

import mission2.car.AssemblyOrder;
import mission2.car.Car;
import mission2.car.part.PartCategory;

import java.util.ArrayList;
import java.util.List;

public class CarView {

    private static final String SPEC_FORMAT = "%-8s : %s";
    private static final String VERDICT_FORMAT = "자동차 부품 조합 테스트 결과 : %s";

    private CarView() {
    }

    public static List<String> runLines(Car car) {
        if (car.findBadCombination().isPresent()) {
            return List.of("자동차가 동작되지 않습니다");
        }
        return car.findBrokenCategory()
                .map(CarView::brokenLines)
                .orElseGet(() -> drivingLines(car));
    }

    public static List<String> testLines(Car car) {
        return car.findBadCombination()
                .map(reason -> List.of(VERDICT_FORMAT.formatted("FAIL"), reason))
                .orElseGet(() -> List.of(VERDICT_FORMAT.formatted("PASS")));
    }

    private static List<String> brokenLines(PartCategory category) {
        String title = category.title();
        return List.of(title + Josa.subject(title) + " 고장나있습니다.",
                "자동차가 움직이지 않습니다.");
    }

    private static List<String> drivingLines(Car car) {
        List<String> lines = specLines(car);
        lines.add("자동차가 동작됩니다.");
        return lines;
    }

    private static List<String> specLines(Car car) {
        List<String> lines = new ArrayList<>();
        for (PartCategory category : AssemblyOrder.categories()) {
            lines.add(SPEC_FORMAT.formatted(category.specLabel(), car.partNameOf(category)));
        }
        return lines;
    }
}
