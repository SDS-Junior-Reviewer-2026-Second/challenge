package mission2.car;

public class CarType implements CarTypeInterface{
    private int carType;

    public int getCarType() {
        return carType;
    }

    public String getCarTypeName(){
        String[] carNames = {"", "Sedan", "SUV", "Truck"};

        return carNames[carType];
    }

    @Override
    public void selectOptions(int a) {
        this.carType = a;
        System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n", a == 1 ? "Sedan" : a == 2 ? "SUV" : "Truck");
    }

    @Override
    public boolean validCheck(int ans) {
        if (ans < 1 || ans > 3) {
            System.out.println("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
            return false;
        }
        return true;
    }
}
