import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Analysis {
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
}