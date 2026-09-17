package assemble.io;

import java.util.Optional;

/** 앱이 사용하는 입출력 추상화. 실제 콘솔과 테스트용 가짜 구현이 있다. */
public interface Console {

    /** 한 줄을 읽는다. 입력이 끝났으면(EOF) empty. */
    Optional<String> readLine();

    void print(String text);

    void println(String text);

    void clear();

    void delay(int millis);
}
