package assemble;

import assemble.io.SystemConsole;

public class Assemble {

    public static void main(String[] args) {
        SystemConsole console = new SystemConsole();
        new AssembleApp(console).run();
        console.close();
    }
}
