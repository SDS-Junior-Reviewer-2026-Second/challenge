package org.example.assemble;

public interface UserInterface {
    void showMenu(AssemblyStep step);

    String readInput();

    void showNumberRequired();

    void showRangeError(AssemblyStep step);

    void showSelection(CarType carType);

    void showSelection(Engine engine);

    void showSelection(BrakeSystem brakeSystem);

    void showSelection(SteeringSystem steeringSystem);

    void showInvalidCar();

    void showBrokenEngine();

    void showRunningCar(VehicleConfiguration configuration);

    void showTestStarted();

    void showTestResult(CompatibilityResult result);

    void showGoodbye();
}
