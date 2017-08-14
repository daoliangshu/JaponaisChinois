package com.daoliangshu.japonaischinois.lettrabulle;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by daoliangshu on 6/30/17.
 */

public class ProjectileState {
    private RectF mRectF;
    private RectF mCollisionRectF;
    private float[] speed = { 0f, 0f};
    private char value;
    private char nextValue;
    private boolean isActive = false;


    public ProjectileState(RectF mRectF, char initValue){
        setRectF(mRectF);
        this.value = initValue;
    }

    public ProjectileState(ProjectileState ps){
        mRectF = new RectF(ps.getRectF());
        mCollisionRectF = new RectF(ps.getCollisionRect());
        speed[0] =ps.dx();
        speed[1] = ps.dy();
        value = ps.getValue();
        nextValue = ps.getNextValue();
        isActive = ps.getIsActive();
    }

    public void setValue(char v){
        Log.d("Value_Changed_CHAR:: ", "value: " + value + "  become " + v);
        this.value = v; this.nextValue = value;

    }
    public void setNextValue(char v){ this.nextValue = v;}
    public void setRectF(RectF rf){

        this.mRectF = rf;
        this.mCollisionRectF = new RectF(0.0f, 0.0f , rf.width()/4, rf.height()/4);
        mCollisionRectF.offsetTo(rf.centerX() - mCollisionRectF.width()/2,
                                rf.centerY() - mCollisionRectF.height()/2);

    }
    public void setSpeed(float dx, float dy){ speed[0] = dx; speed[1] = dy;}
    public void setDx(float dx){ speed[0] = dx; }
    public void setDy(float dy){ speed[1] = dy; }
    public void setIsActive(boolean state){ this.isActive = state; }

    //GET
    public int width(){ return (int)mRectF.width();}
    public int height(){ return (int)mRectF.height();}
    public int left(){ return (int)mRectF.left;}
    public int top(){ return (int)mRectF.top;}
    public int centerX(){ return (int)mRectF.centerX(); }
    public int centerY(){ return (int)mRectF.centerY(); }
    public int right(){ return (int)mRectF.right; }



    public char getValue(){
        Log.d("ReturnValue_CHAR:", "return "+value);
        return this.value; }
    public char getNextValue(){ return this.nextValue;}
    public RectF getRectF(){ return this.mRectF; }
    public float dy(){ return speed[1];}
    public float dx(){ return speed[0];}
    public Rect getRect(){ return new Rect(0, 0, (int)mRectF.width(), (int)mRectF.height());}


    public RectF getCollisionRect(){ return mCollisionRectF;}


    //updates
    public void move(){
        mRectF.offset(speed[0], speed[1]);
    }
    public void updateValue(){
        Log.d("NEXT_CHAR",
                "old: " + value + "  __  new: " + nextValue);
        value = nextValue;
    }
    public void reset(RectF initialPos){
        setSpeed(0.0f, 0.0f);
        isActive = false;
        mRectF = initialPos;
    }
    public boolean getIsActive(){ return isActive; }
}
