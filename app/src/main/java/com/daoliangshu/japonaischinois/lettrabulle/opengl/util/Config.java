package com.daoliangshu.japonaischinois.lettrabulle.opengl.util;

/**
 * Created by daoliangshu on 2017/7/10.
 */

public class Config {

    //Lettrabulle mode
    public static final int DST_FROM_TRANS_1 = 0;
    public static final int DST_FROM_TRANS_MIXED = 1;
    public static final int DST_FROM_TRANS_2 = 3;
    public static int curDstType = DST_FROM_TRANS_MIXED;


    public static final char[] EXCLUDED_LETTERS = new char[]{
        '.', ',', ':', ';', '~', '!', '(', ')', '-', '_', '*','[', ']', '\\',
        '<', '>', '/', '?', '～', '、', '。', '・','！', '＠', '”', '｜',
            '「', '」', '｛', '｝', '‘', '＜', '＞', '？'
    };

    public static boolean useAlternativeCells = true;

    public static int width;
    public static int height;
    public static float bubbleDiameter;
    public static final int BUBBLE_PER_LINE = 8;



    //Locks
    public static final Object lock1 = new Object();
    public static boolean lockTopDownUpdate = false;
    public static final Object lockExplosionUpdate = false;

    public static final float getRatioWH(){
        return (float)width/(float)height;
    }



    /*------Constants--------*/
    public static final int MODE_RANDOM_DATABASE = 0; // Fetch random words from database
    public static final int MODE_VOC_LIST_DATABASE = 1; // from voc list which uses database
    public static final int MODE_FROM_DEFINED_LIST = 30;
    public static final int MODE_VOC_LIST_INDEPENDANT = 2; //from voc list ,which

    /*-----In game config--------*/
    public static int mode = Config.MODE_RANDOM_DATABASE;

    /*---- Speed control ---------------*/
    public static final float SPEED_SLOW = -0.0002f;
    public static final float SPEED_MEDIUM = -0.0005f;
    public static final float SPEED_FAST = -0.001f;
    public static float currentSpeed = SPEED_SLOW;

    public static float POWERUP_FALLING_SPEED = -0.0027f;
    public static float PROJECTILE_SPEED = 0.2f;
    public static final int CELLS_COUNT = 4;

    public static final float BOARD_BOTTOM_GLVIEW_POSY = -0.35f;
    public static final float BOARD_BOTTOM_RATIO = 0.75f;
    public static final float LETTER_CHOOSER_WIDTH_RATIOX = 0.5f;

    public static final int SCORE_UNIT = 20;
    public static final float CURRENT_SCORE_WEIGHT = 0.7f;


    public static  final boolean ACTIVE_DEBUG = true;


    public static float board_top = 1f;
    public static float board_bottom = BOARD_BOTTOM_GLVIEW_POSY;
    public static int gameState = Constants.GAME_STATE_PAUSED;

    public static int getSpeedCode() {
        if (currentSpeed == SPEED_SLOW) return 0;
        else if (currentSpeed == SPEED_FAST) return 2;
        else return 1;
    }
}
