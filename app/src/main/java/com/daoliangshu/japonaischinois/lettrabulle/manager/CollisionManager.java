package com.daoliangshu.japonaischinois.lettrabulle.manager;

import android.graphics.RectF;
import android.util.Log;

import com.daoliangshu.japonaischinois.lettrabulle.ProjectileState;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLineInterface;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Locale;

/**
 * Created by daoliangshu on 6/30/17.
 * -given: pos(x,y) , speed(y) of each line
 * -checks if current launched bubble is correct in each line
 *      -if correct: compute time before collision
 *
 */

public class CollisionManager {
    private ArrayList<CollisionState> cs;


    public CollisionManager(){
       cs = new ArrayList<>();
    }


    public boolean processAll(ProjectileState projState, ArrayList<BubbleLineInterface> lines) {
        cs = new ArrayList<>();
        if(Config.ACTIVE_DEBUG){
            Log.d("PROCESS_COLLIDE_CHAR", "value :: " + projState.getValue());
            Log.d("PROCESS_COLLIDE_CHAR", "sizeLines :: " + lines.size());
            Log.d("SPEED::", "proj: " + projState.dy() + "__line:" + lines.get(0).dy());
        }
        for (BubbleLineInterface bl : lines) {
            if( ((com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLine)bl).getIsVanishing()){
                //ignore vanishing lines
                continue;
            }
            updateLine(projState, bl);
        }
        return false;
    }

    public CollisionState getCollisionState(int i){ return cs.get(i); }
    public int getCollisionStateCount(){ return cs.size(); }

    public boolean updateLine(ProjectileState projState, BubbleLineInterface bl){
        if(bl instanceof com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLine){
            return updateLine(projState, (com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLine)bl);
        }
        Log.d("TEST_COLLIDE_CHAR", "value :: " + projState.getValue());
        ArrayList<Integer> tempIndexes;
        if((tempIndexes = bl.getIndexesOfValueTOGuess(projState.getValue())) != null){
            //If the line has cell(s) to guess with this char value
            for(Integer index: tempIndexes ){
                Log.d("INDEX_CELL", ""+ index);
                //check collision.
                RectF cell = bl.getCollisionRectF(index);
                RectF temp = new RectF(projState.getRectF());
                float timeStart = (cell.bottom - temp.centerY() )/(projState.dy() - bl.dy());
                float timeEnd = (cell.top - temp.centerY()) /(projState.dy() - bl.dy());
                if(timeStart > 200)continue;
                Log.d("TIME:: " , "timeTart: " + timeStart + "   timeEnd: " + timeEnd);

                for(int i=0; i< (int)Math.abs(Math.round(timeEnd-timeStart))+1; i++){
                    RectF temp2= new RectF(cell);
                    RectF tempProj = new RectF(projState.getRectF());
                    temp2.offset(0, bl.dy()*(timeStart + i));
                    tempProj.offset(projState.dx()*(timeStart + i),
                            projState.dy()*(timeEnd + i));
                    if(temp2.bottom < 0 || tempProj.bottom < 0 || tempProj.bottom < temp2.top)continue;
                    //Log.d("COLLLL",
                     //       String.format(Locale.ENGLISH,
                    //                "%s, line: %d , cell %d",
                     //               bl.getWord(), bl.getLineIndex(), index));
                    Log.d("DISTANCE::", "value: " + projState.getValue());
                    if(isCollide(tempProj, temp2)){
                        cs.add(
                                 new CollisionState(
                                         projState.getValue(),
                                         (int)(timeStart + i + 2),
                                        bl.getLineIndex(),
                                index));
                        Log.d("VALUE_COLLIDE_CHAR", "value :: " + projState.getValue());
                        Log.d("VCOLLISION99999", String.format(Locale.ENGLISH,
                                "char: %c intersect in %d with line %d, cell %d",
                                projState.getValue(),
                                (int)(timeStart + i),
                                bl.getLineIndex(),
                                index)
                        );
                        return true;
                    }
                }
            }
            //no candidat that intersects with the projectile found
            return false;
        }else{
            //no candidat found
            return false;
        }
    }


    public boolean updateLine(ProjectileState projState, com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLine bl){
        Log.d("TEST_COLLIDE_CHAR", "value :: " + projState.getValue());
        ArrayList<Integer> tempIndexes;
        if((tempIndexes = bl.getIndexesOfValueTOGuess(projState.getValue())) != null){
            //If the line has cell(s) to guess with this char value
            for(Integer index: tempIndexes ){
                Log.d("INDEX_CELL", ""+ index);
                //check collision.
                RectF cell = bl.getCollisionRectF(index);
                RectF temp = new RectF(projState.getRectF());
                float timeStart = (cell.bottom - temp.centerY() )/(projState.dy() - bl.dy());
                float timeEnd = (cell.top - temp.centerY()) /(projState.dy() - bl.dy());
                if(timeStart > 200)continue;
                Log.d("TIME:: " , "timeTart: " + timeStart + "   timeEnd: " + timeEnd);

                for(int i=0; i< (int)Math.abs(Math.round(timeEnd-timeStart))+1; i++){
                    RectF temp2= new RectF(cell);
                    RectF tempProj = new RectF(projState.getRectF());
                    temp2.offset(0, bl.dy()*(timeStart + i));
                    tempProj.offset(projState.dx()*(timeStart + i),
                            projState.dy()*(timeEnd + i));
                    if(temp2.bottom > Config.board_top || tempProj.bottom < Config.board_bottom ||
                            tempProj.bottom > temp2.top)continue;
                    //Log.d("COLLLL",
                    //       String.format(Locale.ENGLISH,
                    //                "%s, line: %d , cell %d",
                    //               bl.getWord(), bl.getLineIndex(), index));
                    Log.d("DISTANCE::", "value: " + projState.getValue());
                    if(isCollide(tempProj, temp2)){
                        cs.add(
                                new CollisionState(
                                        projState.getValue(),
                                        (int)(timeStart + i + 2),
                                        bl.getLineIndex(),
                                        index));
                        Log.d("VALUE_COLLIDE_CHAR", "value :: " + projState.getValue());
                        Log.d("VCOLLISION99999", String.format(Locale.ENGLISH,
                                "char: %c intersect in %d with line %d, cell %d",
                                projState.getValue(),
                                (int)(timeStart + i),
                                bl.getLineIndex(),
                                index)
                        );
                        return true;
                    }
                }
            }
            //no candidat that intersects with the projectile found
            return false;
        }else{
            //no candidat found
            return false;
        }
    }





    public boolean isCollide(RectF rf1, RectF rf2){
        Log.d("COLLLLL", rf1.toString() + "   " + rf2.toString());

        double x = Math.abs(rf2.centerX() - rf1.centerX());
        double y = Math.abs(rf2.centerY() - rf1.centerY());
        double d = Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));
        Log.d("DISTANCE::", "d: "+d + "_cX: " + rf1.centerX() + "!=__"+ rf2.centerX() +
                   " ;; "+  rf1.centerY() + " _cY: " + rf2.centerY() );
        if(d <= Config.bubbleDiameter ){
            Log.d("VCOLLISION2", rf1.toString() + "   " + rf2.toString());
            return true;
        }else{
            return false;
        }
    }

    public void removeLineByIndex(int index){
        for(int i=0; i< cs.size(); i++){
            if(cs.get(i).lineIndex == index){
                cs.remove(i);
            }
        }
    }

    public CollisionState update() throws ConcurrentModificationException{
        for(CollisionState colState: cs){
            colState.interval -=1;
            Log.d("INTERVAL", "mIntervale: "+ colState.interval);
            if(colState.interval <= 0){
                CollisionState tempCs= new CollisionState(colState);
                cs.remove(colState); //remove collision state to avoid duplicate count
                return tempCs;
            }
        }
        return null;
    }
}
