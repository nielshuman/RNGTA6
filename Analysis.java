import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Analysis {
    // ANSI COLORS (for terminal)
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private static final int MAP_SIZE = 1000;

    public static void showMap(int strength) {
        System.out.println("Generating an array of random numbers...");
        int[][] world = new int[MAP_SIZE][MAP_SIZE];
        long max = Integer.MIN_VALUE;
        long min = Integer.MAX_VALUE;
        long timeStart = System.currentTimeMillis();
        // Filling the map to display
        for (int i = 0; i <= strength; i++) {
            int x = (int) Generator.applyBoundaries(Generator.getRandomValue(), 0, MAP_SIZE);
            int y = (int) Generator.applyBoundaries(Generator.getRandomValue(), 0, MAP_SIZE);
            world[x][y] += 1;
        }
        System.out.printf("Success! (%d ms)%n", System.currentTimeMillis() - timeStart);
        // Printing the map
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                System.out.print(world[x][y] + "\t");
                min = Math.min(min, world[x][y]);
                max = Math.max(max, world[x][y]);
            }
            System.out.println();
        }
        System.out.println("Finished. Map size: " + MAP_SIZE);
        createHeatMap(world, min, max);
    }

    private static void createHeatMap(int[][] world, long min, long max) {
        // Creating an image
        BufferedImage img = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                int value = world[x][y];
                int normalizedValue = (int) (((double) (value - min) / (max - min)) * 255);
                Color color = new Color(normalizedValue, normalizedValue, normalizedValue);
                img.setRGB(x, y, color.getRGB());
            }
        }
        try {
            File imageFile = new File("heatmap.png");
            ImageIO.write(img, "png", imageFile);
            System.out.println("Created heatmap image at: " + imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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