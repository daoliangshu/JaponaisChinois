package com.daoliangshu.japonaischinois.core;

import java.util.Comparator;

/**
 * Created by daoliangshu on 6/15/17.
 */
public class CustomIntegerComparator implements Comparator<Integer[]> {
    @Override
    public int compare(final Integer[] o1, final Integer[] o2) {
        return o2[1].compareTo(o1[1]);
    }
}
