package com.daoliangshu.japonaischinois.sentencereordering;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.daoliangshu.japonaischinois.sentencereordering.manager.DrawableManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by daoliangshu on 6/11/17.
 * Represents a sentence.
 *
 */

public class SentencePartition {
    ArrayList<SentenceCell> cells;
    private RectF bounds;
    private Random rand;
    private DrawableManager dm;
    private Paint bgPaint;

    /**
     *
     * @param boardWidth
     * @param senHeightMax Defines the maximun height that a sentence can take on the screen.
     * @param sentence
     */
    public SentencePartition(int boardWidth, int senHeightMax, String sentence, DrawableManager dm){
        this.dm = dm;
        bgPaint = new Paint();
        bgPaint.setColor(Color.GREEN);
        bgPaint.setStyle(Paint.Style.FILL);
        rand = new Random();
        int partitionerCharacterCount = 0; // '*' is used to indicate that the character surrouding it forms a syntagme.
        int partitionerSpaceCount = 0;
        ArrayList<Integer> tokens = new ArrayList<>();
        for(int i=0; i< sentence.length(); i++){
            if(sentence.charAt(i) == '*'){
                partitionerCharacterCount += 1;
            } else if (sentence.charAt(i) == ' ') {
                partitionerSpaceCount += 1;
            }
        }
        if(partitionerCharacterCount <= 0){
            //The sentence has no partitioners, so choose manually.
            cells = new ArrayList<>();
            int first = 0;
            int last = 0;
            int carry = 0;
            int index = 0;

            ArrayList<Integer> splitDelimiter = getRandomPartition(sentence);




            while(last < sentence.length()){
                last++;
                if(( splitDelimiter.contains(last)) || last == sentence.length()){
                    cells.add(new SentenceCell(
                             sentence.substring(first, last),
                             boardWidth,
                                    index)
                             );
                    index++;
                    first = last;
                }
            }
            Collections.shuffle(cells);
            RectF prevRectF = null;
            for(int i=0; i< cells.size(); i++){
                cells.get(i).setUnorderedIndex(i);
                if( i==0) cells.get(i).moveTo(10,5);
                else{


                    if(prevRectF.right + 50 + cells.get(i).getRectF().width() >= boardWidth){
                        cells.get(i).moveTo(10, prevRectF.bottom + 50);
                    }else{
                        cells.get(i).moveTo(prevRectF.right + 50, prevRectF.top);
                    }


                    //if(cells.get(i).getRectF().right >= boardWidth){
                    //
                    //}
                }
                prevRectF = cells.get(i).getRectF();
            }
            bounds = new RectF(0f, 0f, boardWidth, cells.get(cells.size()-1).getRectF().bottom);
        }

    }

    private ArrayList<Integer> getRandomPartition(String sentence){
        if(sentence.length() <= 1)return new ArrayList<>();
        int max = sentence.length() -1 - sentence.length()/2;
        int num = sentence.length()/2 + Math.abs(rand.nextInt())%max;

        ArrayList<Integer> res = new ArrayList<>();
        int a = -1;
        while(num != 0){
            a = Math.abs(rand.nextInt())%(sentence.length()-1);
            if(!res.contains(a)){
                res.add(a);
                num--;
            }
        }
        return res;
    }

    public ArrayList<SentenceCell> getCellInfos(){ return cells;}

    public RectF getBounds(){ return this.bounds; }

    public void onDraw(Canvas canvas){

        canvas.drawRect(bounds, bgPaint);
        dm.drawShapes(canvas, getCellInfos());
        dm.drawTexts(canvas, getCellInfos());
    }

}
