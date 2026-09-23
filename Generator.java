
public class Generator {
    public static long seed = obtainSeed.byMixedMethods();
    static long multiplier = 1103515245; //keep it under int range to avoid overflow
    static long increment = 12345; //keep it under int range to avoid overflow
    static long mod = 2147483648L; //prime number, keep it under int range to avoid overflow

    private static long generateRandomNumber(int times, long seed) 
    {
        if (times == 0) return seed;
        return generateRandomNumber(times - 1, (multiplier * seed + increment) % mod);
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
