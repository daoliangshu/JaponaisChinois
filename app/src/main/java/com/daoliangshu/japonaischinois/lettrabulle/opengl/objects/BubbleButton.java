package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;

/**
 * Created by daoliangshu on 2017/7/23.
 */

public class BubbleButton {
    public  static final int BTN_DOWN = 0;
    public static final int BTN_UP = 1;


    Bubble mBubble;
    private boolean isPressed = false;

    public  BubbleButton(float x, float y, int BUTTON_CODE){
        if(BUTTON_CODE == Constants.CELL_BTN_QUIT){
            mBubble = new Bubble(0.8f*(Config.getRatioWH()/(float)Config.BUBBLE_PER_LINE),
                    0.8f*(Config.getRatioWH()/(float)Config.BUBBLE_PER_LINE));
        }else{
            mBubble = new Bubble(1.2f*(Config.getRatioWH()/(float)Config.BUBBLE_PER_LINE),
                    1.2f*(Config.getRatioWH()/(float)Config.BUBBLE_PER_LINE));
        }

        mBubble.setBubbleCode(BUTTON_CODE);
        mBubble.setPos(x, y);
    }

    public void draw(float[] mView, float[] mProjection, TextureShaderProgram program){
        mBubble.draw(mView,
                    mProjection,
                    program, isPressed?10f:1f);
    }


    public boolean isPressed(float x, float y, int BTN_MODE){
        if(x > mBubble.getRectF().left &&
                x < mBubble.getRectF().right){
            if(y > mBubble.getRectF().bottom &&
                    y < mBubble.getRectF().top){
                switch(BTN_MODE){
                    case BTN_DOWN:
                        isPressed = true; break;
                    case BTN_UP:
                        isPressed = false; break;
                }
                return true;
            }
        }

        isPressed = false;
        return false;
    }
}
