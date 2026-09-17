import java.util.ArrayList;

class AssemblePolicyRule {

    private boolean usedTogether;

    private ComponentType first;
    private ComponentType second;

    private AssemblePolicyRule(
        boolean usedTogether,
        ComponentType first,
        ComponentType second
    ) {
        this.usedTogether = usedTogether;
        this.first = first;
        this.second = second;
    }

    static AssemblePolicyRule shouldBeUsedTogether(
        ComponentType first,
        ComponentType second
    ) {
        return new AssemblePolicyRule(true, first, second);
    }

    static AssemblePolicyRule shouldNotBeUsedTogether(
        ComponentType first,
        ComponentType second
    ) {
        return new AssemblePolicyRule(false, first, second);
    }

    public boolean shouldUsedTogether() {
        return usedTogether;
    }

    public ComponentType getFirstComponent() {
        return first;
    }

    public ComponentType getSecondComponent() {
        return second;
    }
}

public class AssemblePolicy {

    private ArrayList<AssemblePolicyRule> rules;

    public void addRule(AssemblePolicyRule rule) {
        rules.add(rule);
    }

    public boolean check(Car car) {
        return rules.stream().allMatch(rule -> checkSingleRule(rule, car));
    }

    boolean checkSingleRule(AssemblePolicyRule rule, Car car) {
        if (rule.shouldUsedTogether()) {
            return checkUsedTogether(rule, car);
        } else {
            return checkNotUsedTogether(rule, car);
        }
    }

    private boolean checkUsedTogether(AssemblePolicyRule rule, Car car) {
        return !(
            car.hasComponent(rule.getFirstComponent()) ^
            car.hasComponent(rule.getSecondComponent())
        );
    }

    private boolean checkNotUsedTogether(AssemblePolicyRule rule, Car car) {
        return !(
            car.hasComponent(rule.getFirstComponent()) &&
            car.hasComponent(rule.getSecondComponent())
        );
    }
}
