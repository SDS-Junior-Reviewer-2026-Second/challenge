package mission2.car;

import mission2.car.part.Brakes;
import mission2.car.part.CarTypes;
import mission2.car.part.Engines;
import mission2.car.part.PartCategory;
import mission2.car.part.SteeringSystems;

import java.util.List;

public class AssemblyOrder {

    private static final List<PartCategory> CATEGORIES = List.of(
            CarTypes.CATEGORY,
            Engines.CATEGORY,
            Brakes.CATEGORY,
            SteeringSystems.CATEGORY);

    private AssemblyOrder() {
    }

    public static List<PartCategory> categories() {
        return CATEGORIES;
    }
}
