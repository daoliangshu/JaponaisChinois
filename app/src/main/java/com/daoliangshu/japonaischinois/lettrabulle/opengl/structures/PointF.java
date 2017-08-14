package com.daoliangshu.japonaischinois.lettrabulle.opengl.structures;

/**
 * Created by daoliangshu on 2017/7/6.
 */

public class PointF {
    public float x;
    public float y;
    public PointF(float x, float y){
        this.x = x;
        this.y = y;
    }

    public PointF copy(){ return new PointF(this.x, this.y); }
}
