import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test for the entry point only: does main() correctly wire System.in/out into
 * AssemblyWizard? Wizard behavior itself is covered by AssemblyWizardTest.
 */
class AssembleTest {

    @Test
    void main_wiresStdinAndStdoutIntoWizard() throws Exception {
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream("exit\n".getBytes(StandardCharsets.UTF_8)));
        try {
            String output = ConsoleCapture.captureStdOut(() -> Assemble.main(new String[0]));
            assertThat(output).contains("바이바이");
        } finally {
            System.setIn(originalIn);
        }
    }
}
