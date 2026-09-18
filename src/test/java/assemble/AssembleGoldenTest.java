package assemble;

import assemble.io.FakeConsole;
import assemble.rule.CarInspector;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 리팩토링 전 원본 Assemble 의 출력을 그대로 캡처해 둔 골든 파일과 비교한다.
 * src/test/resources/golden/<name>.in 이 입력, <name>.out 이 기대 출력이다.
 */
class AssembleGoldenTest {

    private static final CarInspector STANDARD_INSPECTOR = CarInspector.standard();

    @ParameterizedTest
    @ValueSource(strings = {
            "run_ok",
            "test_pass",
            "test_fail_sedan_continental",
            "test_fail_suv_toyota",
            "test_fail_truck_wia",
            "test_fail_truck_mando",
            "test_fail_bosch_mismatch",
            "test_multi_violation",
            "run_invalid_combo",
            "run_broken_engine",
            "run_broken_engine_and_invalid",
            "back_navigation",
            "invalid_inputs",
            "exit_immediately"
    })
    void outputMatchesGolden(String scenario) {
        List<String> inputs = readResource("golden/" + scenario + ".in").lines().toList();
        String expected = readResource("golden/" + scenario + ".out");

        FakeConsole console = new FakeConsole(inputs);
        new AssembleApp(console, STANDARD_INSPECTOR).run();

        assertThat(normalize(console.output())).isEqualTo(normalize(expected));
    }

    /** 원본은 println(CRLF) 과 printf("\n")(LF) 이 섞여 있었으므로 줄바꿈을 LF 로 통일해 비교한다. */
    private static String normalize(String s) {
        return s.replace("\r\n", "\n");
    }

    private static String readResource(String path) {
        try (InputStream in = AssembleGoldenTest.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("resource not found: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
