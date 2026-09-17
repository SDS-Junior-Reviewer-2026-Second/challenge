import java.io.InputStream;
import java.io.PrintStream;

public class AssemblyFactory {

    private static final String[] CAR_ART = {
            "        ______________",
            "       /|            |",
            "  ____/_|_____________|____",
            " |                      O  |",
            " '-(@)----------------(@)--'"};

    private AssemblyFactory() {
    }

    public static Assemble createConsoleApp(InputStream in, PrintStream out, boolean animated) {
        return new Assemble(new ConsoleDisplay(in, out, animated), createSteps());
    }

    public static Step[] createSteps() {
        return new Step[]{
                Step.first("차량 타입", "어떤 차량 타입을 선택할까요?", Car.CAR_TYPE,
                        Car.SEDAN, Car.SUV, Car.TRUCK).withBanner(CAR_ART),
                Step.part("엔진", "어떤 엔진을 탑재할까요?", Car.ENGINE,
                        Car.GM, Car.TOYOTA, Car.WIA, Car.BROKEN_ENGINE),
                Step.part("제동장치", "어떤 제동장치를 선택할까요?", Car.BRAKE,
                        Car.MANDO, Car.CONTINENTAL, Car.BOSCH),
                Step.part("조향장치", "어떤 조향장치를 선택할까요?", Car.STEERING,
                        Car.BOSCH, Car.MOBIS),
                Step.action("동작", "어떤 동작을 할까요?",
                        Car.RUN, Car.TEST).withBanner("멋진 차량이 완성되었습니다.")};
    }
}
