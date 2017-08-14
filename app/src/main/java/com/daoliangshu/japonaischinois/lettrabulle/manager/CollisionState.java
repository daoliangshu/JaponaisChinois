package com.daoliangshu.japonaischinois.lettrabulle.manager;

/**
 * Created by daoliangshu on 6/30/17.
 */

public class CollisionState {

    public CollisionState(char value, int interval, int lineIndex, int cellIndex){
        this.value = value;
        this.interval = interval;
        this.lineIndex = lineIndex;
        this.cellIndex = cellIndex;
    }

    public CollisionState(CollisionState cs){
        this.value = cs.value;
        this.interval = cs.interval;
        this.lineIndex = cs.lineIndex;
        this.cellIndex = cs.cellIndex;
    }



    public char value;
    public int interval;
    public int lineIndex;
    public int cellIndex;
}
