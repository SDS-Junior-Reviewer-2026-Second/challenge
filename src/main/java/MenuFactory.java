public class MenuFactory {
    Menu menu;
    private static final int CarType_Q      = 0;
    private static final int Engine_Q       = 1;
    private static final int BrakeSystem_Q  = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test       = 4;

    Menu create(int step) {
        switch (step) {
            case CarType_Q:
                return new CarTypeMenu();
            case Engine_Q:
                return new EngineMenu();
            case BrakeSystem_Q:
                return new BrakeSystemMenu();
            case SteeringSystem_Q:
                return new SteeringSystemMenu();
            case Run_Test:
                return new RunTestMenu();
        }
        return null;
    }
}
