package com.daoliangshu.japonaischinois.lettrabulle;

import android.graphics.Canvas;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLineInterface;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 2016/11/9.
 */

public class Service {

    /***

     * @param lines WordLines
     * @return the index that has reach the bottom if any, or -1
     */
    public static int moveLines(ArrayList<BubbleLineInterface> lines) {
        int needUpdateIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).move() == com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLine.REACH_BOTTOM) needUpdateIndex = i;
        }
        return needUpdateIndex;
    }

    public static int moveLinesBoosted(ArrayList<BubbleLineInterface> lines, boolean needBoost){
        for(BubbleLineInterface bl :lines){
            bl.boost(needBoost);
        }
        return moveLines(lines);
    }




    public static void drawLines(Canvas canvas, ArrayList<BubbleLineInterface> lines) {
        for (int i = 0; i < lines.size(); i++) {
            ((BubbleLine)lines.get(i)).onDraw(canvas);
        }
    }

}
