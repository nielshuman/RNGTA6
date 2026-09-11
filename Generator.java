import java.util.Scanner;

public class Generator {
    private static long seed = 1202;

    private static long generateRandomNumber(int times, long seed) {
        if (times == 0) return seed;
        int multiplier = 21212;
        int increment = 12196;
        int mod = 1000000007;
        return generateRandomNumber(times - 1, (multiplier * seed + increment) % mod); // TODO fix negatives
    }

    public static long getRandomValue() {
        int times = 1;
        seed = generateRandomNumber(times, seed);
        return seed;
    }

    /**
     * It is stated in the project manual that we need this function
     * @param a lower boundary
     * @param b higher boundary (exclusive)
     * @return a (not really)random number
     */
    public static int getRandom(int a, int b){
        return (int) applyBoundaries(getRandomValue(),a,b);
    }

    public static void main(String[] args) {
        // If argument received we draw a map to make research on RNG predictivity. (How random is your RNG?)
        if (args.length > 0 && args[0].equalsIgnoreCase("map")) {
            Additional.showMap(10000000);
            System.exit(0);
        }
        System.out.println("Welcome!");
        Additional.generatingLoop();
    }

    /**
     * Maps the value given to the specific boundaries
     *
     * @param num  number to limit
     * @param from lower boundary
     * @param to   higher boundary (exclusive)
     * @return truncated value
     */
    public static long applyBoundaries(long num, long from, long to) {
        if (from >= to)
            throw new IllegalArgumentException("Cannot apply boundaries because lower boundary is higher or equal to higher boundary");
        long fromZeroDistance = num % (to - from);
        if (fromZeroDistance < 0) // Can happen if num is negative
            fromZeroDistance += (to - from); // Shifting to required boundaries
        return from + fromZeroDistance;
    }
}