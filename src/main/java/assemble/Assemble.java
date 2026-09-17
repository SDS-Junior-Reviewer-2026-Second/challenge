package assemble;

import assemble.io.SystemConsole;
import assemble.rule.CarInspector;
import assemble.rule.CompatibilityRules;

public class Assemble {

    public static void main(String[] args) {
        CarInspector inspector = new CarInspector(CompatibilityRules.ALL);
        new AssembleApp(new SystemConsole(), inspector).run();
    }
}
