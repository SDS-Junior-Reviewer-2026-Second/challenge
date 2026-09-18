package mission2.car.part;

public class CarTypes {

    public static final PartCategory CATEGORY = new PartCategory(
            "차량 타입",
            "어떤 차량 타입을 선택할까요?",
            "차량 타입으로 %s을 선택하셨습니다.",
            "Car Type");

    public static final Part SEDAN = CATEGORY.part("Sedan");
    public static final Part SUV = CATEGORY.part("SUV");
    public static final Part TRUCK = CATEGORY.part("Truck");

    private CarTypes() {
    }
}
