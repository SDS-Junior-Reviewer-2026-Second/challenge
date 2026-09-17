import java.util.ArrayList;
import java.util.List;

public class Car {

    public static final String SEDAN = "Sedan";
    public static final String SUV = "SUV";
    public static final String TRUCK = "Truck";

    public static final String GM = "GM";
    public static final String TOYOTA = "TOYOTA";
    public static final String WIA = "WIA";
    public static final String BROKEN_ENGINE = "고장난 엔진";

    public static final String MANDO = "Mando";
    public static final String CONTINENTAL = "Continental";
    public static final String BOSCH = "Bosch";
    public static final String MOBIS = "Mobis";

    public static final String RUN = "RUN";
    public static final String TEST = "Test";

    public static final int CAR_TYPE = 0;
    public static final int ENGINE = 1;
    public static final int BRAKE = 2;
    public static final int STEERING = 3;

    private static final String[] SPEC_LABELS = {"Car Type", "Engine", "Brake", "Steering"};

    private final String[] parts = new String[SPEC_LABELS.length];

    public void install(int slot, String partName) {
        parts[slot] = partName;
    }

    public String findBadCombination() {
        if (is(CAR_TYPE, SEDAN) && is(BRAKE, CONTINENTAL)) {
            return "Sedan에는 Continental제동장치 사용 불가";
        }
        if (is(CAR_TYPE, SUV) && is(ENGINE, TOYOTA)) {
            return "SUV에는 TOYOTA엔진 사용 불가";
        }
        if (is(CAR_TYPE, TRUCK) && is(ENGINE, WIA)) {
            return "Truck에는 WIA엔진 사용 불가";
        }
        if (is(CAR_TYPE, TRUCK) && is(BRAKE, MANDO)) {
            return "Truck에는 Mando제동장치 사용 불가";
        }
        if (is(BRAKE, BOSCH) && !is(STEERING, BOSCH)) {
            return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";
        }
        return null;
    }

    public List<String> perform(String action) {
        return RUN.equals(action) ? runLines() : testLines();
    }

    private List<String> runLines() {
        if (findBadCombination() != null) {
            return List.of("자동차가 동작되지 않습니다");
        }
        if (is(ENGINE, BROKEN_ENGINE)) {
            return List.of("엔진이 고장나있습니다.", "자동차가 움직이지 않습니다.");
        }
        List<String> lines = specLines();
        lines.add("자동차가 동작됩니다.");
        return lines;
    }

    private List<String> testLines() {
        String badCombination = findBadCombination();
        String verdict = "자동차 부품 조합 테스트 결과 : " + (badCombination == null ? "PASS" : "FAIL");
        return badCombination == null ? List.of(verdict) : List.of(verdict, badCombination);
    }

    private List<String> specLines() {
        List<String> lines = new ArrayList<>();
        for (int slot = 0; slot < parts.length; slot++) {
            lines.add("%-8s : %s".formatted(SPEC_LABELS[slot], parts[slot]));
        }
        return lines;
    }

    private boolean is(int slot, String partName) {
        return partName.equals(parts[slot]);
    }
}
