package com.daoliangshu.japonaischinois.sentencereordering;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.daoliangshu.japonaischinois.sentencereordering.manager.CellInfo;
import com.daoliangshu.japonaischinois.sentencereordering.manager.Config;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 6/8/17.
 * Represents a Cell of the sentence to reorder.
 * A cell contains a part of the sentence that forms a unit ( already ordered)
 */

public class SentenceCell {
    private int posUnordered = -1;
    private ArrayList<Integer> posOrdered;
    private String content;
    boolean borderSpaceLeftRight[] = { false, false }; // hidden spaces
    private CellInfo ci;
    private Paint textPaint;
    private Paint rectPaint;
    private float textPaddingOffsetX = 0;
    private int boardWidth;
    private Rect bounds;
    private int widthCell = 0;

    public SentenceCell(String content, int boardWidth, int orderedIndex){
        this.boardWidth = boardWidth;
        textPaint = new Paint();
        rectPaint = new Paint();
        rectPaint.setColor(Color.BLUE);
        ci = new CellInfo();
        posOrdered = new ArrayList<>();
        posOrdered.add(orderedIndex);
        setContent(content, boardWidth);

    }

    public void setContent(String content, int boardWidth){
        float textSize = boardWidth/Config.RATIO_WIDTH_TO_HEIGHT_SENTENCE;
        this.content = content;
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.RED);
         bounds = new Rect();
        textPaint.getTextBounds(content, 0, content.length(), bounds);
        //getTextBounds give the size including the padding at start and end of the stirng
        Rect b2 = new Rect();
        textPaint.getTextBounds(content,  0, 1, b2);
        textPaddingOffsetX = textPaint.measureText(content.substring(0,1)) - b2.width();
        widthCell = (int)textPaint.measureText(content);
        bounds.set(0 ,0 , widthCell, (int)textSize);
        ci.setRectF(new RectF(bounds));
        //textPaddingOffsetY = textPaint.get
    }

    public CellInfo getCellInfo(){
        Log.d("CELL_INFO_POS", ci.getStringInfo());
        return this.ci;
    }

    public void drawText(Canvas canvas){
        //canvas.drawRect(ci.getRectF(), textPaint);
        canvas.save();
        canvas.translate(ci.getRectF().left, ci.getRectF().top);
        rectPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(bounds, rectPaint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(content, bounds.centerX(), bounds.bottom - textPaint.descent()/2, textPaint);
        canvas.restore();

    }

    public void setUnorderedIndex(int unorderedIndex){
        this.posUnordered = unorderedIndex;
    }

    public void moveTo(float dx, float dy){

        ci.moveRectFTo(dx, dy);}
    public RectF getRectF(){ return ci.getRectF(); }

}
