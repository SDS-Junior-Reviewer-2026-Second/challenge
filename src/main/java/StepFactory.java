public class StepFactory {
    Step step;
    private static final int CarType_Q      = 0;
    private static final int Engine_Q       = 1;
    private static final int BrakeSystem_Q  = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test       = 4;

    Step create(int step, int answer) {
        switch (step) {
            case CarType_Q:
                return new CarType(answer);
            case Engine_Q:
                return new Engine(answer);
            case BrakeSystem_Q:
                return new BrakeSystem(answer);
            case SteeringSystem_Q:
                return new SteeringSystem(answer);
            case Run_Test:
//                if (answer == 1) {
//                    runProducedCar();
//                    delay(2000);
//                } else if (answer == 2) {
//                    System.out.println("Test...");
//                    delay(1500);
//                    testProducedCar();
//                    delay(2000);
//                }
//                break;
                return null;

        }
        return null;
    }

}
