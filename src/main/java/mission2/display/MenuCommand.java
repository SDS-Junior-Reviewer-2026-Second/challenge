package mission2.display;

import mission2.car.Car;

public class MenuCommand {
    MenuInterface menu;

    public MenuCommand(int step){
        if (step == Car.CarType_Q) menu = new CarTypeMenu();
        else if (step == Car.Engine_Q) menu = new EngineMenu();
        else if (step == Car.BrakeSystem_Q) menu = new BrakeMenu();
        else if (step == Car.SteeringSystem_Q) menu = new SteeringMenu();
        else if (step == Car.Run_Test) menu = new RunTestMenu();
    }

    public void command(){
        if(menu!=null) menu.show();
    }
}
