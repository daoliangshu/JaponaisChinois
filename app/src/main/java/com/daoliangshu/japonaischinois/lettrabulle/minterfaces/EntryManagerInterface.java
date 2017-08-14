package com.daoliangshu.japonaischinois.lettrabulle.minterfaces;

/**
 * Created by daoliangshu on 7/29/17.
 */

public interface EntryManagerInterface {

    /*Update*/
    public void removeVoc(int vocIndex);

    /*Setters*/
    public void setVocList(int lessonIndex, int thematicIndex, int level);

    /*Getters*/
    public String getSourceFromId(int vocIndex, int whichSource);

}
