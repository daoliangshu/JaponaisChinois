package com.daoliangshu.japonaischinois.sentencereordering;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.daoliangshu.japonaischinois.sentencereordering.manager.DrawableManager;

/**
 * Created by daoliangshu on 6/17/17.
 */

public class Wave {
    SentencePartition sp;
    private DrawableManager dm;
    private RectF bounds;

    public Wave(int boardWidth, int bottom, DrawableManager dm){
        this.dm = dm;
        sp = new SentencePartition(boardWidth, boardWidth/2, "Je suis une phrase test", dm);
        bounds = new RectF(sp.getBounds());
        bounds.offsetTo(0, bottom - bounds.height());
    }


    public void onDraw(Canvas canvas){
        canvas.save();
        canvas.translate(0, bounds.top);
        sp.onDraw(canvas);
        canvas.restore();
    }
}
