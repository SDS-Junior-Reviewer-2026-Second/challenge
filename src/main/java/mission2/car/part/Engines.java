package mission2.car.part;

public class Engines {

    public static final PartCategory CATEGORY = new PartCategory(
            "엔진",
            "어떤 엔진을 탑재할까요?",
            "%s 엔진을 선택하셨습니다.",
            "Engine");

    public static final Part GM = CATEGORY.part("GM");
    public static final Part TOYOTA = CATEGORY.part("TOYOTA");
    public static final Part WIA = CATEGORY.part("WIA");
    public static final Part BROKEN = CATEGORY.brokenPart("고장난 엔진");

    private Engines() {
    }
}
