public class obtainSeed {
    public static int byTime() {
        return (int) System.currentTimeMillis();
    }

    public static int byPID() {
        return (int) ProcessHandle.current().pid();
    }

    public static int byFreeMemory() {
        return (int) Runtime.getRuntime().freeMemory();
    }

    public static int byMemoryHash() {
        return (int) System.identityHashCode(new Object());
    }

    public static int byMixedMethods() {
        return byTime() ^ byPID() ^ byFreeMemory() ^ byMemoryHash();
    }
}