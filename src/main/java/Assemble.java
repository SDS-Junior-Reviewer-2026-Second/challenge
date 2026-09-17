import org.example.assemble.AssemblyApplication;
import org.example.assemble.CompatibilityPolicy;
import org.example.assemble.ConsoleUserInterface;
import org.example.assemble.ThreadDelay;

import java.util.Scanner;

public class Assemble {
    public static void main(String[] args) {
        try (ConsoleUserInterface userInterface =
                     new ConsoleUserInterface(new Scanner(System.in), System.out)) {
            new AssemblyApplication(
                    userInterface,
                    new CompatibilityPolicy(),
                    new ThreadDelay()
            ).run();
        }
    }
}
