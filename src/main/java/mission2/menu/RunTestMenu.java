package mission2.menu;

import mission2.Car;

import static mission2.AssembleCar.DELAY_MS;
import static mission2.AssembleCar.delay;

public class RunTestMenu implements Menu {
    public void showMenu() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
    }

    public boolean isValidInputRange(int answer) {
        if (answer < 0 || answer > 2) {
            System.out.println("ERROR :: Run 또는 Test 중 하나를 선택 필요");
            return false;
        }
        return true;
    }

    public Menu applyAndGetNextMenu(Car car, int choice) throws InterruptedException {
        if(choice == 0){
            return new CarTypeMenu();
        }
        else if (choice == 1) {
            runProducedCar(car);
            delay(2000);
        } else if (choice == 2) {
            System.out.println("Test...");
            delay(1500);
            testProducedCar(car);
            delay(2000);
        }

        delay(DELAY_MS);
        return this;
    }

    public static void runProducedCar(Car car) {
        if (!getValidComponentResult(car).equals("PASS")) {
            System.out.println("자동차가 동작되지 않습니다");
            return;
        }
        if (car.getEngine().equals("고장난 엔진")) {
            System.out.println("엔진이 고장나있습니다.");
            System.out.println("자동차가 움직이지 않습니다.");
            return;
        }

        printWorkingStauts(car);
    }

    private static void printWorkingStauts(Car car) {
        System.out.printf("Car Type : %s\n", car.getCarType());
        System.out.printf("Engine   : %s\n", car.getEngine());
        System.out.printf("Brake    : %s\n", car.getBrake());
        System.out.printf("Steering : %s\n", car.getSteering());
        System.out.println("자동차가 동작됩니다.");
    }


    private static void testProducedCar(Car car) {
        String result = getValidComponentResult(car);

        if (result.equals("PASS")) System.out.println("자동차 부품 조합 테스트 결과 : PASS");
        else {
            System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
            System.out.println(result);
        }
    }

    public static String getValidComponentResult(Car car) {
        if (car.getCarType().equals("Sedan") && car.getBrake().equals("Continental"))
            return "Sedan에는 Continental제동장치 사용 불가";
        if (car.getCarType().equals("SUV") && car.getEngine().equals("TOYOTA"))
            return "SUV에는 TOYOTA엔진 사용 불가";
        if (car.getCarType().equals("Truck") && car.getEngine().equals("WIA"))
            return "Truck에는 WIA엔진 사용 불가";
        if (car.getCarType().equals("Truck") && car.getBrake().equals("Mando"))
            return "Truck에는 Mando제동장치 사용 불가";
        if (car.getBrake().equals("Bosch") && !car.getSteering().equals("Bosch"))
            return "Bosch제동장치에는 Bosch조향장치 이외 사용 불가";

        return "PASS";
    }
}