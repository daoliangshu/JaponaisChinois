package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;

/**
 * Created by daoliangshu on 2017/7/8.
 */

public interface ObjectInterface {

    static final int POSITION_COMPONENT_COUNT = 2;
    static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    static final int STRIDE = (POSITION_COMPONENT_COUNT +
            TEXTURE_COORDINATES_COMPONENT_COUNT) *
            4;

    public static final int TEX_COLUMN_COUNT = 32;
    public static final int TEX_ROW_COUNT = 8;
    public static final int TEX_UNIT_WIDTH = 64;
    public static final int TEX_UNIT_HEIGHT = 64;

    /*Draw*/
    public void draw();

    /*Binding*/
    public void bindData(TextureShaderProgram textureShaderProgram);

    /*Getters*/
    public float[] getTextCoordinates();
    public int getX_pix();
    public int getY_pix();
    public float getY_Gl();
    public float getX_Gl();

}
