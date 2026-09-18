package mission2.car;

public class BrakeSystem implements BrakeSystemInterface{
    private int brakeSystem;

    public int getBrakeSystem() {
        return brakeSystem;
    }
    public String getBrakeSystemName(){
        return brakeSystem==1? "Mando":
                brakeSystem==2? "Continental":"Bosch";
    }

    @Override
    public void selectOptions(int a) {
        brakeSystem = a;
        String name = a == 1 ? "MANDO" : a == 2 ? "CONTINENTAL" : "BOSCH";
        System.out.printf("%s 제동장치를 선택하셨습니다.\n", name);
    }

    @Override
    public boolean validCheck(int ans) {
        if (ans < 0 || ans > 3) {
            System.out.println("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
            return false;
        }
        return true;
    }
}
