package com.daoliangshu.japonaischinois.lettrabulle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.daoliangshu.japonaischinois.R;


/**
 * Created by daoliangshu on 2016/11/9.
 * Represents the canon,
 * as well as the projectile
 */

public class Canon {

    /*-------Bitmaps--------*/
    private Bitmap canon;
    private Bitmap projectile;
    private Bitmap projAvatar;
    private Bitmap canonBasis;



    private ProjectileState mProjState;

    /*------State-----------*/
    private float angle = 0f;
    private int radius = 0;
    private float activeAngle;
    private Rect pos;
    private Rect rectPreviewProj;
    private Rect rectBoard;
    private float moveUnitX = 1.0f;
    private float moveUnitY = 1.0f;
    private float distance = 0;
    private boolean isDisplayTrajectory = true;
    private Bitmap nextBubble;
    private Paint linePaint = null;
    private Paint circlePaint;
    private int scaleShrinker;
    private int previewVerticalOffset = 10;
    private Paint previewPaint;
    private float textX, textY;


    /*-----References-------*/
    private Context context;
    private LetterChooser letterChooser;

    /*-----------------------*/
    /*-----CONSTRUCTOR-------*/
    /*-----------------------*/
    public Canon(Context context, int centerX, int centerY, int radius, int bottom) {
        this.context = context;
        this.radius = radius;
        canon = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_canon1_0);
        canon = Bitmap.createScaledBitmap(canon, radius * 4, radius * 4, false);
        projectile = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cell1);
        projectile = Bitmap.createScaledBitmap(projectile, radius * 2, radius * 2, false);
        pos = new Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        //rectfProj = new RectF(pos);

        mProjState = new ProjectileState(new RectF(pos), 'a');

        canonBasis = BitmapFactory.decodeResource(context.getResources(), R.drawable.canon_base1);
        int canonBasisHeight = radius * 2;
        if (centerY < bottom) {
            canonBasisHeight = bottom - centerY;
        }
        canonBasis = Bitmap.createScaledBitmap(canonBasis, radius * 3, canonBasisHeight, false);

        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setPathEffect(new DashPathEffect(new float[]{30, 30, 30, 30}, 0));
        linePaint.setAlpha(200);
        linePaint.setStrokeWidth(12);

        //set positions of current letter preview
        scaleShrinker = LB_Config.bubbleDiameter / 2;
        rectPreviewProj = new Rect(pos.centerX() - (mProjState.width() - scaleShrinker) / 2,
                pos.centerY() - (mProjState.height() - scaleShrinker) / 2 + previewVerticalOffset ,
                pos.centerX() + (mProjState.width() - scaleShrinker) / 2,
                pos.centerY() + (mProjState.height() - scaleShrinker) / 2 + previewVerticalOffset);
        previewPaint = new Paint();
        previewPaint.setAlpha(180);
        previewPaint.setAntiAlias(true);

        previewPaint.setFilterBitmap(true);
        previewPaint.setDither(true);
        previewPaint.setColorFilter(new LightingColorFilter(Color.YELLOW, 1));
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(2);
        circlePaint.setColor(Color.BLACK);

        projAvatar = LB_Config.cells[LB_Config.CELLS_COUNT - 1].
                copy(LB_Config.cells[LB_Config.CELLS_COUNT - 1].getConfig(), true);
    }

    /*-------------------------------------*/
    /*------------DRAW & UPDATEs ----------*/
    /*-------------------------------------*/
    public void onDraw(Canvas canvas) {

        if(mProjState.getIsActive()) {
            //draw projectile
            canvas.drawCircle(mProjState.getRectF().centerX(),
                    mProjState.getRectF().centerY(),
                    mProjState.getRectF().width() / 2 + 2, circlePaint);

        }


        canvas.drawBitmap(projAvatar, mProjState.left(), mProjState.top(), null);
        canvas.drawBitmap(canonBasis, pos.left - radius / 2, pos.centerY(), null);
        canvas.save();
        canvas.rotate(angle, pos.centerX(), pos.centerY());
        canvas.drawBitmap(canon, pos.left - radius, pos.top - radius, null);
        canvas.restore();

        //draw letter preview
        canvas.drawCircle(rectPreviewProj.centerX(),
                rectPreviewProj.centerY(),
                rectPreviewProj.width() / 2 + 2, circlePaint);
        //canvas.drawBitmap(projectile, mProjState.getRect(), rectPreviewProj, previewPaint);
        previewPaint.setStyle(Paint.Style.FILL);
        previewPaint.setColor(Color.YELLOW);
        canvas.drawCircle(rectPreviewProj.centerX(),
                            rectPreviewProj.centerY(),
                            50.0f,
                            previewPaint);




        if(this.isDisplayTrajectory) {
            canvas.save();
            canvas.rotate(angle, pos.centerX(), pos.centerY());
            canvas.drawLine(pos.centerX(), pos.centerY(), pos.centerX(), pos.centerY() - 1000, linePaint);
            canvas.restore();
        }
        previewPaint.setColor(Color.BLACK);
        previewPaint.setTextSize(30);
        canvas.drawText(""+getLetter(), textX, textY, previewPaint);
    }

    /**
     *
     * @return true if speed has changed and collision need to be reprossessed
     */
    public boolean move() {
        mProjState.move();
        distance += -LB_Config.PROJECTILE_SPEED;
        if (mProjState.getRectF().bottom < 0) {
            //projectile exit the board: reset projectile
            distance = 0;
            if(this.nextBubble != null) {
                this.projectile = this.nextBubble.copy(nextBubble.getConfig(), true);
                mProjState.updateValue();
            }
            mProjState.reset(new RectF(pos));

        } else if (mProjState.getRectF().left < rectBoard.left) {
            //wall collision left
            mProjState.getRectF().offsetTo(1, mProjState.getRectF().top);
            this.activeAngle = -(180 + this.activeAngle) % 360; //reverse angle
            mProjState.setSpeed(computeDx(), mProjState.dy());
            return true;

        } else if (mProjState.right() > rectBoard.right) {
            mProjState.getRectF().offsetTo(rectBoard.right - mProjState.width(), mProjState.getRectF().top);
            this.activeAngle = (180 - this.activeAngle) % 360;
            mProjState.setSpeed(computeDx(), mProjState.dy());
            return true;
        }
        return false;
    }

    private float computeDx(){
        return moveUnitX * (float) (-LB_Config.PROJECTILE_SPEED * Math.cos(Math.toRadians(this.activeAngle)));
    }
    private float computeDy(){
        return moveUnitY * (float) (-LB_Config.PROJECTILE_SPEED * Math.sin(Math.toRadians(this.activeAngle)));
    }

    /*-------------------------------------*/
    /*------------SETTERS-----------------*/
     /*-------------------------------------*/
    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setDisplayTrajectory(boolean state) {
        this.isDisplayTrajectory = state;
    }

    public void setActiveProjectile(boolean state) {
        mProjState.setIsActive(state);
        if (state) {
            activeAngle = angle + 90;
            mProjState.setSpeed(
                    computeDx(),
                    computeDy());
        } else {
            mProjState.setRectF(new RectF(this.pos));
            mProjState.updateValue();
            mProjState.setSpeed(0.0f, 0.0f);
        }
    }

    public void setBoard(Rect rectBoard) {
        this.rectBoard = rectBoard;
    }

    private void setCurrent(Bitmap cell, char selectedLetter) {
        Log.d("NEWVALUE_CHAR", "new char: " + selectedLetter);
        //this.projectile = cell.copy(cell.getConfig(), true);
        float widths[] ={ 0};
        previewPaint.getTextWidths(""+selectedLetter, widths);
        textY = rectPreviewProj.centerY() + previewPaint.getTextSize()/2 ;
        textX = rectPreviewProj.centerX() - widths[0]/2;
        mProjState.setValue(selectedLetter);
    }

    public void setValue(Bitmap cell, char selectedLetter){
        if(mProjState.getIsActive()){
            setNext(cell, selectedLetter);
        }else{
            setCurrent(cell, selectedLetter);
        }
    }

    /**
     * Set the next bubble to come after the one currently launched
     *
     * @param nextBubble
     * @param selectedLetter
     */
    private void setNext(Bitmap nextBubble, char selectedLetter) {
        //this.nextBubble = nextBubble.copy(nextBubble.getConfig(), true);
        mProjState.setNextValue(selectedLetter);
    }

    public void setLetterChooser(LetterChooser letterChooser) {
        this.letterChooser = letterChooser;
    }

    /*-------------------------------------*/
    /*------------GETTERS------------------*/
     /*-------------------------------------*/
    public int getCanonCenterX() {
        return this.pos.centerX();
    }

    public int getCanonCenterY() {
        return this.pos.centerY();
    }

    public RectF getProjectileRectfF() {
        return mProjState.getRectF();
    }
    public ProjectileState getProjState(){ return mProjState;}

    public float getAngle() {
        return this.angle;
    }

    public char getLetter() {
        return mProjState.getValue();
    }

    public boolean getProjIsActive() {
        return mProjState.getIsActive();
    }

    public void setMoveUnits(float moveUnitX, float moveUnitY) {
        this.moveUnitX = moveUnitX;
        this.moveUnitY = moveUnitY;
    }


}
