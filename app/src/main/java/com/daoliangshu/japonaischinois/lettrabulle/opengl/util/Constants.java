package com.daoliangshu.japonaischinois.lettrabulle.opengl.util;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.PointF;

/**
 * Created by daoliangshu on 2017/7/6.
 */

public class Constants {


    public static final int GROUP_BOARD_COMPONENT = 0;
    public static final int GROUP_INTERFACE_COMPONENT = 1;
    public static final int GROUP_BACKGROUND_COMPONENT = 2;
    public static final int GROUP_BUBBLELINE_COMPONENT = 3;
    public static final int GROUP_EFFECT_COMPONENT = 4;
    public static final int GROUP_LETTER_COMPONENT = 5;


    public static final PointF TEXT_BALL_1[] = {
        new PointF(0.5f, 0.5f),
            new PointF(0.125f, 0.875f),
            new PointF(0.15625f, 0875f),
            new PointF(0.15625f, 1f),
            new PointF(0.125f, 1f),
            new PointF(0.125f, 0.875f)
    };




    public static final float[][] colors = new float[][]{
            new float[]{ 1f, 1f, 1f},
            new float[]{ 1f, 0.5f, 0.5f},
            new float[]{ 0.3f, 1f, 0.3f},
            new float[]{ 0.5f, 0.5f, 1f},
            new float[]{ 1f, 1f, 0.3f},
            new float[]{0.3f, 1f, 1f}
    };


    public static final int CELL_GREEN = 0;
    public static final int CELL_BLUE = 1;
    public static final int CELL_PINK = 2;
    public static final int CELL_YELLOW = 3;
    public static final int CELL_ORANGE = 4;
    public static final int CELL_CANON = 5;
    public static final int CELL_CANON_BASE = 6;
    public static final int CELL_LETTER_CHOOSER = 7;
    public static final int CELL_BG = 8;
    public static final int CELL_CHOOSER_BG = 9;
    public static final int CELL_AVATAR_IDLE = 10;
    public static final int CELL_AVATAR_ACTIVE = 11;
    public static final int CELL2 = 12;
    public static final int CELL_BTN_QUIT = 13;
    public static final int CELL_BTN_POWERUP = 14;
    public static final int CELL_BTN_DELETE_VOC = 15;
    public static final int CELL_STAR = 16;

    public static final int TEX_COLUMN_COUNT = 32;
    public static final int TEX_ROW_COUNT = 8;
    public static final int TEX_UNIT_WIDTH = 64;
    public static final int TEX_UNIT_HEIGHT = 64;

    public static final float BASE_OFFSET_X = 1f/(float)TEX_COLUMN_COUNT;
    public static final float BASE_OFFSET_Y = 1f/(float)TEX_ROW_COUNT;

    public static final int GAME_STATE_ACTIVE = 0;
    public static final int GAME_STATE_OVER = 1;
    public static final int GAME_STATE_PAUSED = 2;



    public static final float[] TEXTURE_COORDINATES = {
            (float)0.0f, (float)0f,                     //0: Green bubble
            (float)1f/TEX_COLUMN_COUNT , (float)0f,
            (float)2f/TEX_COLUMN_COUNT, (float)0f,
            (float)3f/TEX_COLUMN_COUNT, (float)0f,
            (float)4f/TEX_COLUMN_COUNT, (float)0f,

            (float)0.0f, (float)4f/TEX_ROW_COUNT,         //5: Canon
            (float)8f/TEX_COLUMN_COUNT, (float)3f/TEX_ROW_COUNT , //6: Canon Base
            (float)4f/TEX_COLUMN_COUNT, (float)4f/TEX_ROW_COUNT, //7: Letter CHooser
            (float)24f/TEX_COLUMN_COUNT, (float)0f/TEX_ROW_COUNT, // 8 : BG
            (float)12f/TEX_COLUMN_COUNT, (float)0f/TEX_ROW_COUNT, // 9: Chooser BG
            (float)20f/TEX_COLUMN_COUNT, (float)4f/TEX_ROW_COUNT, // 10 : Avartar IDLE
            (float)20f/TEX_COLUMN_COUNT, (float)0f/TEX_ROW_COUNT, // 11 : Avatar ACTIVE
            (float)0.0f, (float)1f/TEX_ROW_COUNT, // 12: Cell2 ( alternative cell )
            (float)0f/TEX_COLUMN_COUNT, (float)2f/TEX_ROW_COUNT, //13: Button quit
            (float)1f/TEX_COLUMN_COUNT, (float)2f/TEX_ROW_COUNT, // 14: btn power up
            (float)2f/TEX_COLUMN_COUNT, (float)2f/TEX_ROW_COUNT, //15: btn delete line
            (float)3f/TEX_COLUMN_COUNT, (float)2f/TEX_ROW_COUNT //16: cell star
};




}
