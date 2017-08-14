package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.graphics.RectF;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 2017/7/14.
 */

public interface BubbleLineInterface {
    public final static int REACH_BOTTOM = 0;
    public final static int VANISH_OVER = 1;
    public final static int NO_EVENT = -1;


    /*Update*/
    void updateSpeed();
    void collide(int cellIndex);
    int move();

    /*Setters*/
    //Pos,speed
    void setPosFromBottom(int bottom);
    void setPos(int p);
    //State
    void setActive(boolean state);
    void setBoost(boolean state);
    //Other
    void setInfo(String infoStr);
    void setWord(String word, int colorIndex);

    /*Getters*/
    //Pos
    int getBottom();
    int getTop();
    float dy();
    RectF getCollisionRectF(int index);
    //State
    boolean getIsVanishing();
    boolean getIsBoosted();
    boolean getIsFinished();
    //Others
    String getInfo();
    int getColorIndex();
    String getWord();
    ArrayList<Integer> getIndexesOfValueTOGuess(char value);
    int getLineIndex();
    String getToGuessLetters();
    int getVocIndex();

}
