package mission2.car;

import mission2.car.part.Part;
import mission2.car.part.PartCategory;
import mission2.car.rule.CombinationRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Car {

    private final Map<PartCategory, Part> installed = new HashMap<>();

    public void install(Part part) {
        installed.put(part.category(), part);
    }

    public boolean has(Part part) {
        return installed.get(part.category()) == part;
    }

    public String partNameOf(PartCategory category) {
        Part part = installed.get(category);
        return part == null ? "" : part.name();
    }

    public Optional<String> findBadCombination() {
        return CombinationRules.findViolation(this);
    }

    public Optional<PartCategory> findBrokenCategory() {
        return AssemblyOrder.categories().stream()
                .filter(category -> isBroken(installed.get(category)))
                .findFirst();
    }

    private static boolean isBroken(Part part) {
        return part != null && part.isBroken();
    }
}
