package mission2.car.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartCategory {

    private final String title;
    private final String question;
    private final String selectionMessage;
    private final String specLabel;
    private final List<Part> parts = new ArrayList<>();

    public PartCategory(String title, String question, String selectionMessage, String specLabel) {
        this.title = title;
        this.question = question;
        this.selectionMessage = selectionMessage;
        this.specLabel = specLabel;
    }

    public Part part(String name) {
        return part(name, name);
    }

    public Part part(String name, String label) {
        return register(new Part(this, name, label, false));
    }

    public Part brokenPart(String name) {
        return register(new Part(this, name, name, true));
    }

    public String title() {
        return title;
    }

    public String question() {
        return question;
    }

    public String specLabel() {
        return specLabel;
    }

    public List<Part> parts() {
        return Collections.unmodifiableList(parts);
    }

    public List<String> partLabels() {
        return parts.stream().map(Part::label).toList();
    }

    String selectionMessage(Part part) {
        return selectionMessage.formatted(part.label());
    }

    private Part register(Part part) {
        parts.add(part);
        return part;
    }
}
