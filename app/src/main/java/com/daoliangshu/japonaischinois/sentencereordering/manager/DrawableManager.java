package com.daoliangshu.japonaischinois.sentencereordering.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.sentencereordering.SentenceCell;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 6/8/17.
 */

public class DrawableManager {

    private Bitmap scaledCellBackgrounds;  //different ratio too match most uses
    private Rect bgRects[];
    //imgView.setColorFilter(ContextCompat.getColor(this, R.color.COLOR_1_DARK));

    public DrawableManager(Context c, Rect boardRect){
        scaledCellBackgrounds = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.rect_bg,
                null);
        bgRects = new Rect[1];

        bgRects[0] = new Rect(0,
                                0,
                (int)(boardRect.width()/Config.RATIO_WIDTH_TO_HEIGHT_SENTENCE),
                (int)(boardRect.width()/Config.RATIO_WIDTH_TO_HEIGHT_SENTENCE)
                                );
        scaledCellBackgrounds = Bitmap.createScaledBitmap(scaledCellBackgrounds,
                bgRects[0].width(),
                bgRects[0].height(),
                true);
    }



    public boolean drawShape(Canvas canvas, CellInfo cellInfo){
        canvas.drawBitmap(scaledCellBackgrounds,
                bgRects[cellInfo.getIndex()],
                cellInfo.getRectF(),
                cellInfo.getPaint()
        );
        return true;
    }

    public boolean drawShapes(Canvas canvas, ArrayList<SentenceCell> cells){
        //here first draws the backgrounds of the cells.
        for(SentenceCell ci: cells){
            drawShape(canvas, ci.getCellInfo());
        }
        return true;
    }

    public boolean drawTexts(Canvas canvas, ArrayList<SentenceCell> cells){
        for(SentenceCell ci: cells){
            ci.drawText(canvas);
        }
        return true;
    }


    /**
     * Choose the background that corresponds the most to the ratio w/h of the cell
     * @param cellWidth
     * @param cellHeight
     * @return
     */
    public int findBgId(int cellWidth, int cellHeight){
        return -1;
    }
}
