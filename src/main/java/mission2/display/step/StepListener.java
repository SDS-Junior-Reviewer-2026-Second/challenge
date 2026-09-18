package mission2.display.step;

import mission2.car.CarAction;
import mission2.car.part.Part;

public interface StepListener {

    void partSelected(Part part);

    void actionSelected(CarAction action);
}
