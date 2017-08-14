package com.daoliangshu.japonaischinois.lettrabulle.opengl.structures;

/**
 * Created by daoliangshu on 2017/7/7.
 */
public class TextObject {

    public char text;
    public float x;
    public float y;
    public float[] color;

    public TextObject()
    {
        text = 'L';
        x = 0f;
        y = 0f;
        color = new float[] {1f, 1f, 1f, 1.0f};
    }

    public TextObject(char txt, float xcoord, float ycoord)
    {
        text = txt;
        x = xcoord;
        y = ycoord;
        color = new float[] {1f, 1f, 1f, 1.0f};
    }
}