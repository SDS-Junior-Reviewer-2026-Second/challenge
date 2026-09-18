package mission2.car;

public class SteeringSystem implements SteeringSystemInterface{
    private int steeringSystem;

    public int getSteeringSystem() {
        return steeringSystem;
    }

    @Override
    public void selectOptions(int a) {
        this.steeringSystem = a;
        String name = a == 1 ? "BOSCH" : "MOBIS";
        System.out.printf("%s 조향장치를 선택하셨습니다.\n", name);
    }

    @Override
    public boolean validCheck(int ans) {
        if (ans < 0 || ans > 2) {
            System.out.println("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
            return false;
        }
        return true;
    }

    public String getSteeringSystemName() {
        return steeringSystem==1? "Bosch":"Mobis";
    }
}
