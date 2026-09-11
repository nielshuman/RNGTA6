public class Generator
{
    public static int generateRandomNumber(int a, int b, int times, int seed)
    {
        if(times==0) return seed;
        int A = 21212;
        int B = 12196;
        int mod = 1000000007;//random prime number
        return generateRandomNumber(A, B, times-1, (A*seed+B)%mod);
    }
    public static void main(String[] args) 
    {
        int a = 1;
        int b = 90;
        int times = 10;
        int seed = 1212;
        System.out.println(generateRandomNumber(a, b, times, seed));
    }
}