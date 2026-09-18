package assemble.model;

/** 메뉴에서 번호로 고르는 항목. 부품과 RUN/Test 동작이 모두 해당한다. */
public interface MenuOption {
    /** 메뉴에 표시되고 사용자가 입력하는 번호. */
    int code();

    /** 메뉴와 선택 메시지에 표시되는 이름. */
    String displayName();
}
