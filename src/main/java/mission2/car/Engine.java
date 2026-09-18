package mission2.car;

public class Engine implements EngineInterface{
    private int engine;

    public int getEngine() {
        return engine;
    }

    public String getEngineName() {
        String[] engNames = {"", "GM", "TOYOTA", "WIA"};
        return engNames[engine];
    }

    @Override
    public void selectOptions(int a) {
        this.engine = a;
        String name = a == 1 ? "GM" : a == 2 ? "TOYOTA" : a == 3 ? "WIA" : "고장난 엔진";
        System.out.printf("%s 엔진을 선택하셨습니다.\n", name);
    }

    @Override
    public boolean validCheck(int ans) {
        if (ans < 0 || ans > 4) {
            System.out.println("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
            return false;
        }
        return true;
    }
}
