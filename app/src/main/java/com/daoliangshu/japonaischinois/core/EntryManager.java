package com.daoliangshu.japonaischinois.core;

import android.util.Log;

import com.daoliangshu.japonaischinois.core.data.Settings;
import com.daoliangshu.japonaischinois.core.db.DBHelper;
import com.daoliangshu.japonaischinois.core.db.StatisticalDatabase;
import com.daoliangshu.japonaischinois.lettrabulle.minterfaces.EntryManagerInterface;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by daoliangshu on 7/29/17.
 * Separate the input of the lettrabulle game from the mechanizm
 * -Contain vocabulary to display,
 * -Provides methods to fetch next
 */

public class EntryManager implements EntryManagerInterface {
    private ArrayList<HashMap<String,String>> mVocList;
    private Random rand = new Random();
    private StatisticalDatabase statDb;
    private DBHelper dbHelper;
    private String curSetName;
    private int curSubsetIndex;
    private int curParams[] = new int[]{ -1, -1 ,-1 };
    private int curIndex = -1;
    private int[] mHistory = new int[256];
    private int first, last, current = 0;

    /*-------------------------------------------------------*/
    /*----------------------CONSTRUCTOR----------------------*/
    /*-------------------------------------------------------*/
    public EntryManager(DBHelper dbHelper, StatisticalDatabase statisticalDatabase){
        this.dbHelper = dbHelper;
        this.statDb = statisticalDatabase;
    }

    /*-------------------------------------------------------*/
    /*----------------------UPDATE, RESET--------------------*/
    /*-------------------------------------------------------*/
    public void removeVoc(int vocIndex){
        return;
    }
    public void updateList(){
        setVocList(curParams[0], curParams[1], curParams[2]);
    }
    public void resetHistory(){
        first = 0;
        last = 0;
        current = 0;
    }
    public void close(){
        dbHelper.close();
        statDb.close();
    }
    public void previous(){
        if(first != current){
            current -= 1;
            if(current < 0)current = mHistory.length-1;
        }
    }
    public void next(){
        if(last != current){
            current  = (current + 1)% mHistory.length;
        }else{
            int prev= current;
            current  = (current + 1)% mHistory.length;
            last = current;
            if(first == last)first = (first + 1)%mHistory.length;
            int count = 0;
            do{
                mHistory[current] = getRandomIndex();
                count +=1;
            }while(mHistory[current] == mHistory[prev] && count < 15);
        }
    }

    /*-------------------------------------------------------*/
    /*----------------------SETTERS--------------------------*/
    /*-------------------------------------------------------*/
    public void setVocList(int lessonIndex, int thematicIndex, int level){
        curParams[0] = lessonIndex;
        curParams[1] = thematicIndex;
        curParams[2] = level;
        ArrayList<Integer> idsList = dbHelper.getIdsByFilter(lessonIndex, thematicIndex, level);
        if(idsList == null)return;
        curSetName = String.format(Locale.ENGLISH,
                "le:%d|the:%d|lv:%d|src1:%s|src2:%s|tg:%s",
                lessonIndex, thematicIndex, level, DBHelper.source1, DBHelper.source2, DBHelper.target);
        if(statDb.getInfoRow(curSetName) != null){
            curSubsetIndex = statDb.getSubsetCount(curSetName) -1;
        }else{
            statDb.createSubSet(curSetName, Settings.ENTRY_BY_SUBSET, idsList);
            curSubsetIndex = statDb.getSubsetCount(curSetName) -1;
        }
        HashMap<String, String> mLog = statDb.getInfoRow(curSetName);
        for(Map.Entry<String, String> entry: mLog.entrySet()){
            Log.d("ROW_VALUE", entry.getKey() + " -> " + entry.getValue());
        }
        ArrayList<Integer> mIds = statDb.getIdsFromSubset(curSetName, curSubsetIndex);
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();
        for(Integer mId : mIds){
            HashMap<String, String> entry = dbHelper.getEntryById(mId);
            if(entry != null)temp.add(entry);

        }
        if(temp.size() > 0)curIndex = 0;
        mVocList =  temp;
        resetHistory();
        setCurrentWordInddex(getRandomIndex());
    }
    public void setCurrentWordInddex(int wordIndex){
        if(wordIndex >= 0 && mVocList != null && wordIndex < mVocList.size()){
            mHistory[current] = wordIndex;
        }
    }

    /*-------------------------------------------------------*/
    /*----------------------GETTERS--------------------------*/
    /*-------------------------------------------------------*/
    public String getSourceFromId(int vocIndex, int whichSource){
        if(vocIndex < 0)return "Invalid";
        if(vocIndex >= mVocList.size())return "out of bound: getSourceFromId";
        switch(whichSource){
            case 1:
                return mVocList.get(vocIndex).get(DBHelper.source1);
            case 2:
                return mVocList.get(vocIndex).get(DBHelper.source2);
            default:
                return "[Invalid Source]";

        }
    }
    private int getRandomIndex(){
        return Math.abs(rand.nextInt() % mVocList.size());
    }
    public String getWord(int index ,String colKey){
        if(colKey.equals(DBHelper.getPhoneticTable(DBHelper.target))){
            if(!mVocList.get(index).containsKey(colKey) ||
                    mVocList.get(index).get(colKey) == null ||
                    mVocList.get(index).get(colKey).equals("") ){
                return mVocList.get(index).get(DBHelper.target);
            }
        }
        return mVocList.get(index).containsKey(colKey)?mVocList.get(index).get(colKey):"";
    }
    public String getInfo(int vocIndex, int tableSource){
        if(tableSource == Config.DST_FROM_TRANS_1){
            if(!DBHelper.target.equals(DBHelper.getPhoneticTable(DBHelper.target)) &&
                    mVocList.get(vocIndex).get(DBHelper.getPhoneticTable(DBHelper.target)) != null){
                return mVocList.get(vocIndex).get(DBHelper.getPhoneticTable(DBHelper.target));
            }
        }else if(tableSource == Config.DST_FROM_TRANS_2){
            if(!DBHelper.target.equals(DBHelper.getPhoneticTable(DBHelper.target)) &&
                    mVocList.get(vocIndex).get(DBHelper.target) != null){
                return mVocList.get(vocIndex).get(DBHelper.target);
            }
        }
        return null;
    }
    /**
     *
     * @param index : -1 for random index
     * @return a String formated to be processed
     */
    private String getVocFormated(int index){
        if(index == -1)index = getRandomIndex();
        String str = null;
        if(Config.curDstType == Config.DST_FROM_TRANS_1){
            return getWord(index, DBHelper.target)+ ";" + Config.DST_FROM_TRANS_1 + ";" + index;
        }
        else if(Config.curDstType == Config.DST_FROM_TRANS_2){
            str = getWord(index,DBHelper.getPhoneticTable(DBHelper.target)) + ";" + Config.DST_FROM_TRANS_2 + ";" + index;
        }
        else if( Config.curDstType == Config.DST_FROM_TRANS_MIXED && rand.nextBoolean()){
            str = getWord(index,DBHelper.getPhoneticTable(DBHelper.target)) +
                    ";" + Config.DST_FROM_TRANS_2 + ";" + index;
        }
        if(str == null || str.length() <= 0 || str.charAt(0) == ';'){
            str = getWord(index,DBHelper.target) + ";" + Config.DST_FROM_TRANS_1 + ";" + index;
        }
        return str;
    }
    public ArrayList<HashMap<String, String>> getVocList(){
        return mVocList;
    }
    public int getVocCount(){ return mVocList != null?mVocList.size(): -1;}
    public String getRandomWord(){
        return getVocFormated(-1);
    }
    public int getCurrentSubsetIndex(){ return curSubsetIndex; }
    public String getCurrentSetName(){ return curSetName; }
    public int getCurrentWordIndex(){ return mHistory[current]; }
    public HashMap<String, String> getCurrentWord(){
        return mVocList.get(mHistory[current]);
    }
    public DBHelper getDB(){ return this.dbHelper; }
}
