package mission2.display.step;

import mission2.car.part.PartCategory;
import mission2.display.Josa;

import java.util.List;

public class PartStep extends Step {

    private final PartCategory category;

    PartStep(PartCategory category, String backLabel, List<String> banner) {
        super(category.question(), backLabel, category.partLabels(), banner);
        this.category = category;
    }

    @Override
    public void select(int code, StepListener listener) {
        listener.partSelected(category.parts().get(indexOf(code)));
    }

    @Override
    public String rangeError() {
        String title = category.title();
        return "%s%s 1 ~ %d 범위만 선택 가능"
                .formatted(title, Josa.topic(title), category.parts().size());
    }
}
