package mission2.display;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("조사")
class JosaTest {

    @Test
    @DisplayName("받침이 있으면 은, 없으면 는을 붙인다")
    void picksTopicParticle() {
        assertThat(Josa.topic("엔진")).isEqualTo("은");
        assertThat(Josa.topic("차량 타입")).isEqualTo("은");
        assertThat(Josa.topic("제동장치")).isEqualTo("는");
    }

    @Test
    @DisplayName("받침이 있으면 이, 없으면 가를 붙인다")
    void picksSubjectParticle() {
        assertThat(Josa.subject("엔진")).isEqualTo("이");
        assertThat(Josa.subject("제동장치")).isEqualTo("가");
    }
}
