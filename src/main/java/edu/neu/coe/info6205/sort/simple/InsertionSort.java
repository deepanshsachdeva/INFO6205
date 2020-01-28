package edu.neu.coe.info6205.sort.simple;

import java.util.Arrays;

public class InsertionSort<X extends Comparable<X>> implements Sort<X> {

    /**
     * Constructor for InsertionSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public InsertionSort(Helper<X> helper) {
        this.helper = helper;
    }

    public InsertionSort() {
        this(new Helper<>("InsertionSort"));
    }

    @Override
    public void sort(X[] xs, int from, int to) {
        for (int i = from; i < to; i++) {
            // Invariant 1: elements xs[from..i] are in order
            // TO BE IMPLEMENTED ...
            X key = xs[i];
            int j = i-1;

            while (j>=0 && helper.less(key, xs[j]))
            {
                helper.swap(xs ,from, to, j+1, j);
                j = j-1;
            }
            xs[j+1] = key;
            // ... END IMPLEMENTATION
        }
    }

    @Override
    public String toString() {
        return helper.toString();
    }

    @Override
    public Helper<X> getHelper() {
        return helper;
    }

    private final Helper<X> helper;
}
