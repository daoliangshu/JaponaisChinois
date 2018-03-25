package com.daoliangshu.japonaischinois.core.db;

import android.provider.BaseColumns;

/**
 * Created by quentin on 18-2-28.
 */

public class DatabaseContract {

    public static final int DATABASE_VERSION        = 1;
    private static final String COMMA_SEP           = ",";
    private static final String TEXT_TYPE           = " TEXT";
    private static final String INT_TYPE            = " INTEGER";

    public static abstract class DICO_TABLE implements BaseColumns {
        public static final String TABLE_NAME_DICO = "dico";
        public final static String COL_ID = "_id";
        public final static String COL_ZH = "zh_1";
        public final static String COL_FR = "fr_1";
        public final static String COL_JP = "jp_1";
        public final static String COL_FR_2 = "fr_2"; // will store genre
        public final static String COL_ZH_2 = "zh_1"; //will store zhuyin
        public final static String COL_JP_2 = "jp_2"; //store kanji
        public final static String COL_EN_1 = "en_1";
        public final static String COL_EN_2 = "en_1";
        public final static String COL_LESSON = "lesson";
        public final static String COL_THEMATIC = "thematic";
        public final static String COL_LEVEL = "lv";


        public static final String CREATE_TABLE_DICO = "CREATE TABLE " +
                TABLE_NAME_DICO + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COL_JP + TEXT_TYPE + COMMA_SEP +
                COL_JP_2 + TEXT_TYPE + COMMA_SEP +
                COL_ZH + TEXT_TYPE + COMMA_SEP +
                COL_LESSON + INT_TYPE + COMMA_SEP +
                COL_FR + TEXT_TYPE + COMMA_SEP +
                COL_FR_2 + TEXT_TYPE + COMMA_SEP +
                COL_THEMATIC + INT_TYPE + COMMA_SEP +
                COL_EN_1 + TEXT_TYPE + COMMA_SEP +
                COL_LEVEL + INT_TYPE + ")";
    }
}
