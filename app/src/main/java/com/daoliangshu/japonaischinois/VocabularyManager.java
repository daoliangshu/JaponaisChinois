package com.daoliangshu.japonaischinois;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by daoliangshu on 3/11/17.
 * The main purpose of this class is to provide ways to
 * order the vocubulary display
 */

public class VocabularyManager {
    private Random rand = new Random();
    private int historyIndexes[] = new int[255];
    private ArrayList<WeightedVoc> voc;
    private int first = 0;
    private int last = 0;
    private int current = -1;
    private int size;

    public VocabularyManager(int size){
        this.size = size;
        voc = new ArrayList<>();
        for(int i=0; i < size; i++){
            voc.add(new WeightedVoc());
        }
        if(size > 0){
            current = 0;
            //Randomize first word fetched
            historyIndexes[current] = Math.abs(rand.nextInt())%size;
        }
    }


    public void previous(){
        if(current != first){
            current -= 1;
            if(current < 0)current = historyIndexes.length-1;
        }
    }

    public void next(){
        if(current != last){
            current = (current + 1) % historyIndexes.length;
        }
        else if(size > 6){
            //(1) fethch 5 randomize index
            HashMap<Integer, Float> mCandidates = new HashMap<>();
            for(int i=0; i< 5; i++){
                int k;
                do {
                    k = Math.abs(rand.nextInt()) % size;
                }while(k == historyIndexes[current]);
                if(mCandidates.containsKey(k))mCandidates.
                        put(k, mCandidates.get(k)+ voc.get(k).weight*0.7f);
                mCandidates.put(k, voc.get(k).weight*1f);
            }
            int nextWordIndex = getIndexWithMaxWeight(mCandidates);
            setCurrent(nextWordIndex);
        }
        else{
            setCurrent(Math.abs(rand.nextInt())%size);
        }
        voc.get(historyIndexes[current]).decreaseWeight(1);
    }

    public void setCurrent(int index){
        if(last == current) last = (current + 1) % historyIndexes.length;
        current = (current + 1) %historyIndexes.length;
        if( last == first) first = (first + 1) %historyIndexes.length;
        historyIndexes[current] = index;
    }

    private int getIndexWithMaxWeight(HashMap<Integer, Float> index_weight){
        int max = -1;
        float maxWeight = -1.0f;
        if(index_weight.size() <= 0)return -1;
        for(Map.Entry entry: index_weight.entrySet()){
            if((float)entry.getValue() > maxWeight){
                max = (int)entry.getKey();
                maxWeight = (float)entry.getValue();
            }
        }
        if(maxWeight <= 0.0f){
            //restore some weight for random
            voc.get(Math.abs(rand.nextInt())%size).increaseWeight(1);
        }
        return max;
    }

    public int getIndex(){ return current<0?-1:historyIndexes[current]; }


}


class WeightedVoc{
    public int count;
    public float weight = 1.0f;

    private int update_range = 10;

    public void decreaseWeight(int time){
        if(weight > 0 && time > 0)weight /= ((float)time + 0.2f);
    }

    public void increaseWeight(int time){
        if(time > 0){
            weight += (float)time* 0.20f;
        }
        else if(--update_range <= 0){
            weight += 0.05f;
            update_range = 10;
        }
    }
}