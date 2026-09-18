public class obtainSeed {
    public static long byTime() {
        return (long) System.currentTimeMillis();
    }

    public static long byPID() {
        return (long) ProcessHandle.current().pid();
    }

    public static long byFreeMemory() {
        return (long) Runtime.getRuntime().freeMemory();
    }

    public static long byMemoryHash() {
        return (long) System.identityHashCode(new Object());
    }

    public static long byMixedMethods() {
        return byTime() ^ byPID() ^ byFreeMemory() ^ byMemoryHash();
    }
}