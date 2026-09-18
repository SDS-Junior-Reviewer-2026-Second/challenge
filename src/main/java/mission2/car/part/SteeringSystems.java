package mission2.car.part;

public class SteeringSystems {

    public static final PartCategory CATEGORY = new PartCategory(
            "조향장치",
            "어떤 조향장치를 선택할까요?",
            "%s 조향장치를 선택하셨습니다.",
            "Steering");

    public static final Part BOSCH = CATEGORY.part("Bosch", "BOSCH");
    public static final Part MOBIS = CATEGORY.part("Mobis", "MOBIS");

    private SteeringSystems() {
    }
}
