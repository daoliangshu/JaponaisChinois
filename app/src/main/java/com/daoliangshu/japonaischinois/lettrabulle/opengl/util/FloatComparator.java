package com.daoliangshu.japonaischinois.lettrabulle.opengl.util;

import java.util.Comparator;

/**
 * Created by daoliangshu on 2017/7/15.
 */
public class FloatComparator implements Comparator<Float[]> {
    @Override
    public int compare(final Float[] o1, final Float[] o2) {
        return o2[1].compareTo(o1[1]);
    }
}
