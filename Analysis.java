import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Analysis {
    /*
    Heatmap functions
     */
    public static void showMap(int strength, int MAP_SIZE) {
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
        // Printing the map (console)
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                System.out.print(world[x][y] + "\t");
                min = Math.min(min, world[x][y]);
                max = Math.max(max, world[x][y]);
            }
            System.out.println();
        }
        System.out.println("Min: " + min + " Max: " + max);
        System.out.println("Finished. Map size: " + MAP_SIZE);
        createHeatMapImage(world, min, max, MAP_SIZE);
    }

    private static void createHeatMapImage(int[][] world, long min, long max, int MAP_SIZE) {
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
            Desktop.getDesktop().open(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Count same numbers functions
     */
    public static void countSameNumbers(int min, int max, int strength) {
        Map<Integer, Integer> randomNumbers = new HashMap<>();
        for (int i = 0; i <= strength; i++) {
            int randomNumber = Generator.getRandom(min, max);
            randomNumbers.put(randomNumber, randomNumbers.getOrDefault(randomNumber, 0) + 1);
        }
        System.out.println("The most frequently repeated numbers (order:asc):");
        randomNumbers.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder())).forEach(entry -> {
            if (entry.getValue() != 1)
                System.out.printf("Num %d: %d times\n", entry.getKey(), entry.getValue());
        });
    }
}