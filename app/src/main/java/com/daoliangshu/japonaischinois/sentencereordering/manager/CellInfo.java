package com.daoliangshu.japonaischinois.sentencereordering.manager;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by daoliangshu on 6/11/17.
 * Stores the information to draw the cell
 * Includes the rect of the board, and the background index to use from the DrawableManager
 */

public class CellInfo {
    private RectF mRect;
    private int bgId = -1;
    private Paint paint;


    public CellInfo(){
        mRect = new RectF(0,0, 70, 70);
        bgId = 0;
    }

    public String getStringInfo(){
        return "l:"+mRect.left+";t:"+mRect.top+";r:"+mRect.right+";b:"+mRect.bottom;
    }

    public void setRectF(RectF r){ this.mRect = r; }

    public void moveRectFTo(float dx, float dy){
        this.mRect.offsetTo(dx,dy);
    }


    //Getters:
    public RectF getRectF(){ return this.mRect; }
    public Rect getRect(){ return new Rect((int)mRect.left,(int)mRect.right,(int)mRect.width(),(int)mRect.height()); }
    public Paint getPaint(){ return paint; }
    public int getIndex(){ return this.bgId; }


}
