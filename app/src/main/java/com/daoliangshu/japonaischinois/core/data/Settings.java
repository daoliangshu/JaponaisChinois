package com.daoliangshu.japonaischinois.core.data;

import android.content.Context;
import android.preference.PreferenceManager;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.core.EntryManager;

/**
 * Created by daoliangshu on 2/5/17.
 * Here are stored the current user settings
 */

public class Settings {

    public static final int ENTRY_BY_SUBSET = 20;
    public static final boolean REQUEST_UPDATE = true;

    public static final int ENTRY_WORD_ONLY = 0;
    public static final int ENTRY_SEN_ONLY = 1;
    public static final int ENTRY_50_50 = 2;

    public static final int WORD_LINEAR_NEXT = 11;
    public static final int WORD_WEIGHTED_RANDOM_NEXT = 12;

    public static int getCurIntervalPos(){
        switch(curInterval){
            case 9000000: return 0;
            case 3000: return 1;
            case 4000: return 2;
            case 10000: return 3;
            case 15000: return 4;
            case 30000: return 5;
            default: return 0;
        }
    }
    public static int curVocChooserMode = 0; //0:lesson, 1:category
    public static int curLesson = 1;
    public static int curCategory = 0; //index corresponds to string-array value
    public static int curInterval = 100000;
    public static int curNextWordPolicy = WORD_WEIGHTED_RANDOM_NEXT;
    public static float curEmptyRatio = 0.5f;
    public static boolean isNightMode = false;
    public static boolean isAutoSpeak = false;
    public static boolean hideWord = false;
    public static boolean hideTrans = false;
    public static boolean hidePron = false;
    public static int entryType = ENTRY_WORD_ONLY;


    //grammar number of pages per lessons
    public static int[] grNumPages= { 3, 3, 0, 0, 0, 0, 0, 2, 1, 1, 2, 1, 1, 0, 0, 0};

    public static EntryManager dbEntryManager;

    public static void loadSettings(Context c){
        curLesson = Integer.parseInt(PreferenceManager.
                getDefaultSharedPreferences(c).
                getString(c.getString(R.string.pref_lesson_chooser_key),
                        c.getString(R.string.pref_lesson_chooser_default)));

        curVocChooserMode = 0;
    }
}
