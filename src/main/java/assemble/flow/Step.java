package assemble.flow;

import assemble.model.BrakeSystem;
import assemble.model.CarType;
import assemble.model.Engine;
import assemble.model.Part;
import assemble.model.Parts;
import assemble.model.SteeringSystem;

import java.util.List;

/** 조립 화면의 단계. 각 단계가 자기 메뉴 문구와 선택지를 가진다. */
public enum Step {
    CAR_TYPE(
            List.of(
                    "        ______________",
                    "       /|            |",
                    "  ____/_|_____________|____",
                    " |                      O  |",
                    " '-(@)----------------(@)--'",
                    "==============================="),
            "어떤 차량 타입을 선택할까요?",
            null,
            CarType.values(),
            "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능"),
    ENGINE(
            List.of(),
            "어떤 엔진을 탑재할까요?",
            "뒤로가기",
            Engine.values(),
            "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능"),
    BRAKE(
            List.of(),
            "어떤 제동장치를 선택할까요?",
            "뒤로가기",
            BrakeSystem.values(),
            "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능"),
    STEERING(
            List.of(),
            "어떤 조향장치를 선택할까요?",
            "뒤로가기",
            SteeringSystem.values(),
            "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능"),
    RUN_TEST(
            List.of("멋진 차량이 완성되었습니다."),
            "어떤 동작을 할까요?",
            "처음 화면으로 돌아가기",
            RunAction.values(),
            "ERROR :: Run 또는 Test 중 하나를 선택 필요");

    private final List<String> headerLines;
    private final String question;
    private final String backLabel;
    private final Part[] options;
    private final String rangeError;

    Step(List<String> headerLines, String question, String backLabel, Part[] options, String rangeError) {
        this.headerLines = headerLines;
        this.question = question;
        this.backLabel = backLabel;
        this.options = options;
        this.rangeError = rangeError;
    }

    public List<String> headerLines() {
        return headerLines;
    }

    public String question() {
        return question;
    }

    /** 0번 항목의 이름. 뒤로가기를 지원하지 않으면 null. */
    public String backLabel() {
        return backLabel;
    }

    public Part[] options() {
        return options;
    }

    public String rangeError() {
        return rangeError;
    }

    public boolean allowsBack() {
        return backLabel != null;
    }

    public boolean hasOption(int code) {
        return Parts.fromCode(options, code).isPresent();
    }

    public Step next() {
        return this == RUN_TEST ? RUN_TEST : values()[ordinal() + 1];
    }

    /** 0 입력 시 이동할 단계. 마지막 화면에서는 처음으로 돌아간다. */
    public Step back() {
        if (this == RUN_TEST) {
            return CAR_TYPE;
        }
        return values()[Math.max(0, ordinal() - 1)];
    }
}
