package com.daoliangshu.japonaischinois;

/**
 * Created by daoliangshu on 2/5/17.
 * Here are stored the current user settings
 */

public class Settings {
    public static final boolean REQUEST_UPDATE = true;

    //Mode: define what is displayed ( WORD or SENTENCE)
    public static final int ENTRY_WORD_ONLY = 0;
    public static final int ENTRY_SEN_ONLY = 1;
    public static final int ENTRY_50_50 = 2;


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
    public static int curLesson = 1;
    public static int curInterval = 100000;
    public static float curEmptyRatio = 0.5f;
    public static boolean isNightMode = false;
    public static boolean isAutoSpeak = false;
    public static boolean hideWord = false;
    public static boolean hideTrans = false;
    public static boolean hidePron = false;
    public static int entryType = ENTRY_WORD_ONLY;
}
