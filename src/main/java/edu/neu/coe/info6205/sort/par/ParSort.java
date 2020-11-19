package edu.neu.coe.info6205.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.ForkJoinPool;
/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
class ParSort {

    public static int cutoff = 1000;
//    public static int threads;

    public static void sort(int[] array, int from, int to) {
        if (to - from < cutoff) Arrays.sort(array, from, to);
        else {
            CompletableFuture<int[]> parsort1 = parsort(array, from, from + (to - from) / 2); // TO IMPLEMENT
            CompletableFuture<int[]> parsort2 = parsort(array, from + (to - from) / 2, to); // TO IMPLEMENT
            CompletableFuture<int[]> parsort = parsort1.thenCombine(parsort2, (xs1, xs2) -> {
                int[] result = new int[xs1.length + xs2.length];
                // TO IMPLEMENT
                int i = 0, j = 0, k = 0;

                int xs1_len = xs1.length;
                int xs2_len = xs2.length;

                while(i < xs1_len && j < xs2_len){
                    if(xs1[i] <= xs2[j])
                        result[k++] = xs1[i++];
                    else
                        result[k++] = xs2[j++];
                }

                while(i < xs1_len)
                    result[k++] = xs1[i++];

                while(j < xs2_len)
                    result[k++] = xs2[j++];

                for(int ix=0; ix<result.length; ix++)
                    array[from+ix] = result[ix];

                return result;
            });

            parsort.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
//            System.out.println("# threads: "+ ForkJoinPool.commonPool().getRunningThreadCount());
            parsort.join();
        }
    }

    private static CompletableFuture<int[]> parsort(int[] array, int from, int to) {

        return CompletableFuture.supplyAsync(
                () -> {
                    int[] result = new int[to - from];
                    // TO IMPLEMENT
                    sort(array, from, to);

                    for(int i = from; i < (to-from); i++)
                        result[i] = array[i];

                    return result;
                }
        );
    }
}