package lvc.cds;

import java.util.Random;

import ods.ChainedHashMap;
import ods.LinearProbedHashMap;
import ods.SkipListSet;
import ods.BubbaHashMap;

/**
 * Hello world!
 */
public final class App {
    private static Random r = new Random();

    /**
     * Run a method and return the number of MS it takes to execute.
     * 
     * @param rble The Runnable you want me to run.
     * @return double The time in MS.
     */
    public static double timeInMS(Runnable rble) {
        final double convert = 1_000_000.0;
        var start = System.nanoTime();
        rble.run();
        return (System.nanoTime() - start) / convert;
    }

    public static void timings() {
        for (int s = 100; s <= 100_000_000; s *= 2) {
            final int size = s;
            SkipListSet<Integer> sls = new SkipListSet<>();

            var elapsed = timeInMS(() -> {
                for (int i = 0; i < size; ++i) {
                    int v = r.nextInt(size * 10);
                    sls.add(v);
                }
            });

            final int[] targets = new int[10_000];
            for (int i = 0; i < targets.length; ++i) {
                targets[i] = r.nextInt(size * 10);
            }

            elapsed = timeInMS(() -> {
                for (int i = 0; i < 10_000; i++) {
                    sls.find(targets[i]);
                }
            });

            System.out.printf("%d,%1.3f%n", size, elapsed);
        }
    }

    public static void checkAvgHeight() {
        SkipListSet<Integer> sls = new SkipListSet<>();
        int size = 100_000;

        for (int i = 0; i < size; ++i) {
            int v = r.nextInt(size * 10);
            sls.add(v);
        }

        System.out.println("Avg height = " + sls.averageHeight());

    }

    public static void printList() {
        SkipListSet<Integer> sls = new SkipListSet<>();
        int size = 10;
        int[] data = new int[size];

        for (int i = 0; i < size; ++i) {
            int v = r.nextInt(size * 10);
            data[i] = v;
            sls.add(v);
        }

        sls.print();

        System.out.println("\nRemoving " + data[5]);
        sls.remove(data[5]);
        sls.print();

        System.out.println("\nRemoving " + data[1]);
        sls.remove(data[1]);
        sls.print();

        System.out.println("\nRemoving " + 120);
        sls.remove(120);
        sls.print();

    }

    public static void testChainedHashMap() {
        ChainedHashMap<String, Integer> map = new ChainedHashMap<>(32);

        for (int i = 0; i < 62; ++i) {
            map.put("key" + i, i);
        }

        if (map.containsKey("key21")) {
            System.out.println("map contains key21");
        }

        if (!map.containsKey("key123")) {
            System.out.println("map does not contain key123");
        }

        Integer v = map.get("key42");
        if (v != null) {
            System.out.println("key 42 paired with " + 42);
        }

        map.print();
    }

    public static void testLinearHashMap() {
        // This is a really incomplete test.
        final int SIZE = 100_000;
        LinearProbedHashMap<Integer, String> lphm = new LinearProbedHashMap<>();
        int[] targets = new int[SIZE];

        for (int i = 0; i < SIZE; ++i) {
            int arr = r.nextInt();
            lphm.put(arr, "Boo");
            targets[i] = arr;
        }

        var res = timeInMS(() -> {
            for (int i = 0; i < SIZE; i++) {
                lphm.containsKey(targets[i]);
            }
        });

        System.out.printf("time to search for all entries is %,1.4f%n", res);
        lphm.printStats();
    }

    public static void bubba(double load) {
        int[] search = new int[1_000_000];
        Random rand = new Random();
        BubbaHashMap<Integer, Integer> bubba = new BubbaHashMap<>((int)(1000000/load)+1,load);
       
        for (int i = 0; i < 1_000_000; i++) {
            int pick = rand.nextInt();
            bubba.bubbaPut(pick, i);
            search[i] = pick;
        }

        int count = 0;
        double probe = 0.0;

        for (int y = 0; y < 10_000; y++) {
            count += bubba.bubbaGetProbe(search[y]);
            probe++;
        }

        System.out.println("The average number of probes for " + load + " was: " + count / probe);
    }

    public static void linear(double load) {
        int[] search = new int[1_000_000];
        Random rand = new Random();
        LinearProbedHashMap<Integer, Integer> bubba = new LinearProbedHashMap<>((int)(1000000/load)+1,load);
       
        for (int i = 0; i < 1_000_000; i++) {
            int pick = rand.nextInt();
            bubba.put(pick, i);
            search[i] = pick;
        }

        int count = 0;
        double probe = 0.0;

        for (int y = 0; y < 10_000; y++) {
            count += bubba.linearGetProbe(search[y]);
            probe++;
        }

        System.out.println("The average number of probes for " + load + " was: " + count / probe);
    }

    public static void main(String[] args) {
        Double[] lambda = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.95, 0.99};
        
        System.out.println("Bubba and his cousinbrother Cletus");
        for(var a : lambda){
            bubba(a);
        }

        System.out.println();
        System.out.println("LinearProbedHashMap");
        for(var a : lambda){
            linear(a);
        }

        //Yes Bubba's ideas do allow for a higher load factor. We see this when we are countng the 
        //number of average probes. If we analyze the data we can see that we have a low amount of probes
        //until 90% load factor. However in the LinearProbedHashMap we have a low amount of probes until 
        //a 70% load factor. Therefore, we see a about a 20% better result with Bubba's HashMap over
        //the LinearProbedHashMap. This being said, I would reccommed a 70% load factor for the
        //LinearProbedHashMap and a 90% load factor for Bubba's HashMap to maintain through a resizing
        //policy.
        
    }
}
