package com.daoliangshu.japonaischinois.core.minterface;

/**
 * Created by daoliangshu on 8/1/17.
 */

public interface StatisticalDatabaseInterface {
    static final int SEARCH_BY_THEMATIC = 0;
    static final int SEARCH_BY_LEVEL = 0;

    Integer[] getIdSet(final int SEARCH_VALUE, final int SEARCH_MODE);

}

