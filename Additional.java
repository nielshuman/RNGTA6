import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Additional {
    // ANSI COLORS (for terminal)
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private static final int MAP_SIZE = 50;

    public static void showMap(int strength) {
        System.out.println("Generating an array of random numbers...");
        long timeStart = System.currentTimeMillis();
        List<Long> randomlyGeneratedNumbers = new ArrayList<>();
        for (int i = 0; i <= strength; i++) {
            randomlyGeneratedNumbers.add(Generator.getRandomValue());
        }
        System.out.printf("Success! (%d ms)%n", System.currentTimeMillis() - timeStart);
        // Filling the map to display
        int[][] world = new int[MAP_SIZE][MAP_SIZE];
        for (int i = 1; i < randomlyGeneratedNumbers.size(); i = i + 2) {
            long randomNumber1 = randomlyGeneratedNumbers.get(i - 1);
            long randomNumber2 = randomlyGeneratedNumbers.get(i);
            int x = (int) Generator.applyBoundaries(randomNumber1, 0, MAP_SIZE);
            int y = (int) Generator.applyBoundaries(randomNumber2, 0, MAP_SIZE);
            world[x][y] += 1;
        }
        // Printing the map
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                System.out.print(world[x][y] + "\t");
            }
            System.out.println();
        }
        System.out.println("Finished. Map size: " + MAP_SIZE);
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
                    System.out.println(ANSI_RED + "ERROR: boundaries can not be same numbers. Please, type again." + ANSI_RESET);
                    continue;
                }
                if (from > to) {
                    System.out.println(ANSI_RED + "ERROR: Lower boundary must be less than higher boundary. Please, type again." + ANSI_RESET);
                    continue;
                }
                System.out.println("Boundaries applied (from %d to %d)".formatted(from, to));
            } else {
                System.out.println("Generating a new value using previous boundaries (from %d to %d)!".formatted(from, to));
            }
            // Printing a random value
            System.out.println(ANSI_BLUE + "Generated: " + Generator.applyBoundaries(Generator.getRandomValue(), from, to) + ANSI_RESET);
            // Do we need to update boundaries (or to exit) (asking user)
            System.out.println("Do you want to update the boundaries you typed (y/n)? To exit type 'exit'");
            String asnwer = sc.nextLine();
            if (asnwer.equalsIgnoreCase("exit")) {
                System.out.println("Bye!");
                break;
            }
            savePreviousBoundaries = asnwer.equalsIgnoreCase("n");
        }
        sc.close();
    }
}