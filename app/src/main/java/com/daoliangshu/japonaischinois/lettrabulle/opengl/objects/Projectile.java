package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.graphics.RectF;
import android.util.Log;

import com.daoliangshu.japonaischinois.lettrabulle.ProjectileState;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.PointF;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;

/**
 * Created by daoliangshu on 2017/7/10.
 */

public class Projectile {
    public static final int UPDATE_NO_EVENT = -1;
    public static final int UPDATE_RESETING = 0;
    public static final int UPDATE_WALL_COLLISION = 1;
    private float factorResetSpeedY = 3f;



    public FilledBubble mBubble;
    private PointF origin;
    private RectF mCollisionRectF;
    private float speedMagnitude = 0.05f;
    private float[] speed = { 0f, 0f};
    private char value;
    private char nextValue;
    private boolean isAcceptingCollision = false;
    private float transparency = 1f;


    public ProjectileState proState;

    //reset
    private int resetState = 0;
    private boolean isReseting = false;

    public Projectile(float xOrigin, float yOrigin){
        mBubble = new FilledBubble();
        origin = new PointF(xOrigin - Config.bubbleDiameter/2f, yOrigin + Config.bubbleDiameter/2f);
        mBubble.setPos(origin.x, origin.y);
        float offsetX = 0.20f;
        float offsetY = offsetX * Config.getRatioWH();
        proState = new ProjectileState(new RectF(-offsetX + xOrigin,
                                    +offsetY + yOrigin,
                                    offsetX + xOrigin,
                                    -offsetY + yOrigin),
                                    'a');



        proState.setSpeed(Config.PROJECTILE_SPEED, Config.PROJECTILE_SPEED);
    }


    /*------------------------------------------------*/
    /*--------------DRAW------------------------------*/
    /*------------------------------------------------*/
    public void draw(float[] mView, float[] mProjection, TextureShaderProgram program){
        mBubble.bubble.draw(mView, mProjection, program,transparency );
    }

    /*------------------------------------------------*/
    /*--------------INIT,RESET------------------------*/
    /*------------------------------------------------*/
    public void reset(){
        isAcceptingCollision = false;
        if(isReseting){
            if(resetState == 0){
                transparency -= 1.0f;
                if(transparency < 0f) transparency = 0f;
                if(getCenterYF() > 3f) {
                    resetState = 1;
                    speed[0] = 0f;
                    speed[1] = 0.07f * factorResetSpeedY;
                    mBubble.setPos(origin.x, origin.y - 3f);
                    transparency = 1f;
                }
            }else if(resetState == 1){
                if(getCenterYF()  >= origin.y){
                    transparency = 1f;
                    mBubble.bubble.updateLetter();
                    mBubble.setPos(origin.x, origin.y);
                    resetState = 0;
                    isReseting = false;
                    desactivate();
                }
            }

        }else{
            isReseting = true;
        }

    }




    /*------------------------------------------------*/
    /*--------------UPDATE---------------------------*/
    /*------------------------------------------------*/
    public int update(){
        mBubble.bubble.offsetTo(speed[0], speed[1]);
        if(mBubble.bubble.getY_Gl() > 1f + Config.bubbleDiameter ||
                mBubble.bubble.getY_Gl() < -1f - Config.bubbleDiameter ||
                    isReseting){
            reset();
            return UPDATE_RESETING;
        }else if(  mBubble.bubble.getX_Gl() < -Config.getRatioWH() + Config.bubbleDiameter/2f ||
                mBubble.bubble.getX_Gl() > Config.getRatioWH() - Config.bubbleDiameter/2f){
            speed[0] = -speed[0]; // reverse x speed component
            //Note: Should recheck collision
           return UPDATE_WALL_COLLISION;
        }
        return UPDATE_NO_EVENT;
    }

    private void desactivate(){
        speed[0] = 0.0f;
        speed[1] = 0.0f;
        isAcceptingCollision = false;
    }

    public void activate(float angleDegree){
        speed[0] = speedMagnitude*(float)Math.cos(Math.toRadians(angleDegree + 90f));
        speed[1] = speedMagnitude*(float)Math.sin(Math.toRadians(angleDegree + 90f));
        isAcceptingCollision = true;
    }

    /*------------------------------------------------*/
    /*--------------SETTERS---------------------------*/
    /*------------------------------------------------*/
    public void setLetter(char letter){
        if(isAcceptingCollision){

            this.nextValue = letter;
        }else{
            mBubble.setLetter(value);
            this.value = letter;
            this.nextValue = letter;
        }

    }
    /*------------------------------------------------*/
    /*--------------GETTERS---------------------------*/
    /*------------------------------------------------*/
    //Positions
    public float getCenterXF(){
        return mBubble.bubble.getRectF().centerX();
    }
    public float getCenterYF(){
        return mBubble.bubble.getRectF().centerY();
    }
    //State
    public boolean getIsReseting(){
        return this.isReseting;
    }
    public boolean getIsMoving(){
        return !(isAcceptingCollision || isReseting);
    }
    public boolean getIsAcceptingCollision() {
        return isAcceptingCollision;
    }
    public boolean getIsDrawnLetter(){
        return !getIsMoving() || (isReseting && resetState == 1);
    }

    //Others
    public char getLetter(){ return this.value; }
    public float getTransparency(){ return this.transparency; }
    public ProjectileState getProjectileState(){

        proState.setRectF(mBubble.bubble.getRectF());
        proState.setValue(getLetter());
        proState.setSpeed(speed[0], speed[1]);
        Log.d("PROJ_STATE", "x:  " + proState.getRectF().centerX() + "    y:  " +
                proState.getRectF().centerY());
        Log.d("PROJ_STATE", "dx " + proState.dx() +
                "  dx:  " + proState.dy());
        return proState;

    }

}
