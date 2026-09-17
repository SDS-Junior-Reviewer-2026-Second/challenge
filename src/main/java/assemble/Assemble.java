package assemble;

import assemble.io.SystemConsole;
import assemble.rule.CarInspector;

public class Assemble {

    public static void main(String[] args) {
        new AssembleApp(new SystemConsole(), CarInspector.standard()).run();
    }
}
