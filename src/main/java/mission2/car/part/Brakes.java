package mission2.car.part;

public class Brakes {

    public static final PartCategory CATEGORY = new PartCategory(
            "제동장치",
            "어떤 제동장치를 선택할까요?",
            "%s 제동장치를 선택하셨습니다.",
            "Brake");

    public static final Part MANDO = CATEGORY.part("Mando", "MANDO");
    public static final Part CONTINENTAL = CATEGORY.part("Continental", "CONTINENTAL");
    public static final Part BOSCH = CATEGORY.part("Bosch", "BOSCH");

    private Brakes() {
    }
}
