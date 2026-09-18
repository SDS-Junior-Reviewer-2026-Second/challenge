package mission2;

import mission2.car.Car;
import mission2.display.Display;

import java.util.Scanner;

public class Assemble {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Car car = new Car();
        Display display = new Display();

        while (true) {
            display.displayStep(car);

            //exit 입력 시 null
            String buf = selectOption(sc);
            if (buf == null) break;

            // int 변환 실패시 실패 시 null
            Integer answer = parseValue(buf, car);
            if (answer == null) continue;

            car.executeStep(answer);
        }

        sc.close();
    }

    private static String selectOption(Scanner sc) {
        String buf = sc.nextLine().trim();

        if (buf.equalsIgnoreCase("exit")) {
            System.out.println("바이바이");
            return null;
        }
        return buf;
    }

    private static Integer parseValue(String buf, Car car) {
        int answer;
        try {
            answer = Integer.parseInt(buf);
        } catch (NumberFormatException e) {
            System.out.println("ERROR :: 숫자만 입력 가능");
            car.delay(800);
            return null;
        }

        return answer;
    }


}