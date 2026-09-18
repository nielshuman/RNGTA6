import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // If argument received we draw a map to make research on RNG predictivity. (How
        // random is your RNG?)
        if (args.length > 0 && args[0].equalsIgnoreCase("map")) {
            Analysis.showMap(10000000);
            System.exit(0);
        }
        System.out.println("""
                Welcome!
                Choose seed:
                1 (default). By TIME, PID, FREE MEM and MEM HASH
                2. Custom seed
                3. By System time
                4. By PID
                5. By Free Memory
                6. By Memory Hash""");
        Scanner sc = new Scanner(System.in);
        String answer = sc.nextLine();
        switch (answer) {
            case "1", "" -> Generator.seed = obtainSeed.byMixedMethods();
            case "2" -> {
                System.out.print("Type your seed: ");
                Generator.seed = sc.nextLong();
                sc.nextLine(); // \n is in Scanner's buffer - we need to remove it
            }
            case "3" -> Generator.seed = obtainSeed.byTime();
            case "4" -> Generator.seed = obtainSeed.byPID();
            case "5" -> Generator.seed = obtainSeed.byFreeMemory();
            case "6" -> Generator.seed = obtainSeed.byMemoryHash();
            default -> {
                System.out.println("Invalid option.");
                System.exit(1);
            }
        }
        System.out.println("Seed applied: " + Generator.seed);
        generatingLoop();
    }

    public static void generatingLoop() {
        long from = 0;
        long to = 0;
        Scanner sc = new Scanner(System.in);
        boolean savePreviousBoundaries = false;
        while (true) {
            if (!savePreviousBoundaries) {
                // Collecting boundaries from the user
                System.out.println("Please, type boundaries you want to set");
                System.out.print("Lower boundary: ");
                from = sc.nextLong();
                System.out.print("Higher boundary (exclusive): ");
                to = sc.nextLong();
                sc.nextLine(); // \n is in Scanner's buffer - we need to remove it
                if (from == to) {
                    System.out.println(ANSI.RED + "ERROR: boundaries can not be same numbers. Please, type again." + ANSI.RESET);
                    continue;
                }
                if (from > to) {
                    System.out.println(ANSI.RED + "ERROR: Lower boundary must be less than higher boundary. Please, type again."
                            + ANSI.RESET);
                    continue;
                }
                System.out.println("Boundaries applied (from %d to %d)".formatted(from, to));
            } else {
                System.out.println(
                        "Generating a new value using previous boundaries (from %d to %d)!".formatted(from, to));
            }
            // Printing a random value
            System.out.println(ANSI.BLUE + "Generated: "
                    + Generator.applyBoundaries(Generator.getRandomValue(), from, to) + ANSI.RESET);
            // Do we need to update boundaries (or to exit) (asking user)
            System.out.println("Do you want to update the boundaries you typed (y/N)? To exit type 'exit'");
            String answer = sc.nextLine();
            if (answer.equalsIgnoreCase("exit")) {
                System.out.println("Bye!");
                break;
            }
            savePreviousBoundaries = answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("");
        }
        sc.close();
    }
}