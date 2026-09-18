package mission2.car.part;

public class Part {

    private final PartCategory category;
    private final String name;
    private final String label;
    private final boolean broken;

    Part(PartCategory category, String name, String label, boolean broken) {
        this.category = category;
        this.name = name;
        this.label = label;
        this.broken = broken;
    }

    public PartCategory category() {
        return category;
    }

    public String name() {
        return name;
    }

    public String label() {
        return label;
    }

    public boolean isBroken() {
        return broken;
    }

    public String selectionMessage() {
        return category.selectionMessage(this);
    }
}
