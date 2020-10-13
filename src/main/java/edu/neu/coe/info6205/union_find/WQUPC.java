/*
 * Copyright (c) 2017. Phasmid Software
 */
package edu.neu.coe.info6205.union_find;

import edu.neu.coe.info6205.util.Benchmark_Timer;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Weighted Quick Union with Path Compression
 */
public class WQUPC {
    private final int[] parent;   // parent[i] = parent of i
    private final int[] size;   // size[i] = size of subtree rooted at i
    private int count;  // number of components

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     *
     * @param n the number of sites
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public WQUPC(int n) {
        count = n;
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public void show() {
        for (int i = 0; i < parent.length; i++) {
            System.out.printf("%d: %d, %d\n", i, parent[i], size[i]);
        }
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int count() {
        return count;
    }

    /**
     * Returns the component identifier for the component containing site {@code p}.
     *
     * @param p the integer representing one site
     * @return the component identifier for the component containing site {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    public int find(int p) {
        validate(p);
        int root = p;

        /* Two Pass */
//        while (root != parent[root]) {
//            root = parent[root];
//        }
//        while (p != root) {
//            int newp = parent[p];
//            parent[p] = root;
//            p = newp;
//        }

        /* Grandparent */
        while(root != parent[root]){
            parent[root] = parent[parent[root]];
            root = parent[root];
        }
        return root;
    }

    // validate that p is a valid index
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
        }
    }

    /**
     * Returns true if the the two sites are in the same component.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @return {@code true} if the two sites {@code p} and {@code q} are in the same component;
     * {@code false} otherwise
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site {@code p} with the
     * the component containing site {@code q}.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;
        // make smaller root point to larger one
        if (size[rootP] < size[rootQ]) {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
        count--;
    }

    public static void main(String[] args) {
        int[] sizes = {100000, 200000, 400000, 800000, 1000000};

        Random r = new Random();

        System.out.printf("%-10s %-10s %-10s\n", "N", "#1", "#2");
        for (int k = 0; k < sizes.length; k++) {
            int N = sizes[k];

            /**
             * Benchmarking #1 : HWQU without PC
             */
            Supplier<UF_HWQUPC> hwqu_supplier = () -> {
                return new UF_HWQUPC(N, false);
            };

            Consumer<UF_HWQUPC> hwqu_consumer = uf -> {
                while (uf.components() != 1) {
                    int i = r.nextInt(N);
                    int j = r.nextInt(N);
                    uf.connect(i, j);
                }
            };

            Benchmark_Timer<UF_HWQUPC> bm1 = new Benchmark_Timer<>("Benchmarking UF_HWQU with N = "+N, hwqu_consumer);

            /**
             * Benchmarking #2 : WQUPC with PC using grandparent fix
             */
            Supplier<WQUPC> wqupc_supplier = () -> {
                return new WQUPC(N);
            };

            Consumer<WQUPC> wqupc_consumer = uf -> {
                while (uf.count() != 1) {
                    int i = r.nextInt(N);
                    int j = r.nextInt(N);
                    uf.union(i, j);
                }
            };

            Benchmark_Timer<WQUPC> bm2 = new Benchmark_Timer<>("Benchmarking WQUPC with N = "+N, wqupc_consumer);

            /**
             * Comparing Benchmark #1 & #2
             */
            double t1 = bm1.runFromSupplier(hwqu_supplier,50);
            double t2 = bm2.runFromSupplier(wqupc_supplier,50);

            System.out.printf("%-10s %-10.2f %-10.2f\n", N, t1, t2);
        }
    }
}
