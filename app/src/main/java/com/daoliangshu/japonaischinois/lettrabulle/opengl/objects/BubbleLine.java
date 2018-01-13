package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.graphics.RectF;
import android.util.Log;

import com.daoliangshu.japonaischinois.core.data.Settings;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.LetterManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by daoliangshu on 2017/7/10.
 */

public class BubbleLine implements BubbleLineInterface{
    /*----Class variable -----*/
    public final static int MAX_LETTER = 50;
    public final static int REACH_BOTTOM = 0;
    public final static int VANISH_OVER = 1;
    public final static int NO_EVENT = -1;

    public ArrayList<FilledBubble> mBubbles;
    public float ratioWH;


    /*-----State the line-----*/
    private boolean isActive= true;
    private boolean isBoosted = false;
    /*--Vanishing state control variables*/
    private int vanishCounter = 0;
    private int vanishMax = 60;
    private boolean isVanishing = false;

    /*Describe Word Content*/
    private String word;
    private int lineIndex = -1;
    private int cellColorIndex = 0;
    private int letterCount = 0;
    private int lineCount = 0;
    private RectF rectfLine;
    private ArrayList<Integer> guessLetterIndexes;
    private ArrayList<Character> guessLetterValues;

    private int tableSource = -1;
    private int vocIndex = -1; //Index of the voc in the EntryManager instance
    private String info; // Info to display when line is vanishing

    private int lineOffset = 0; //from topLeft cell index before drawing the first letter
    private float[] speed = { 0.0f, Config.currentSpeed};
    private static Random rand = new Random();


    /*Draw scratch matrix*/
    private float[] scratch = new float[16];


    /*-----------------------------------*/
    /*----------CONSTRUCTOR--------------*/
    /*-----------------------------------*/
    public BubbleLine(int lineIndex) {
        ratioWH = Config.getRatioWH();
        this.lineIndex = lineIndex;
        setWord("init");
    }

    /*-----------------------------------*/
    /*-------DRAWs------------------------*/
    /*-----------------------------------*/

    public void draw( float[] mView, float[] mProjection, TextureShaderProgram mTextProgram) {
        if (isActive && letterCount > 0  && rectfLine != null) {
            if(getIsVanishing()){
                float trans = 1f - ((float)vanishCounter/(float)vanishMax);
                for(FilledBubble fb: mBubbles){
                    fb.bubble.draw(mView, mProjection, mTextProgram, trans);
                }
                return;
            }
           for(FilledBubble fb: mBubbles){
               fb.bubble.draw(mView, mProjection, mTextProgram);
           }
        }
    }

    public void drawLetters(float[] mView,
                            float[] mProjection,
                            LetterManager mLetterManager,
                            TextureShaderProgram mTextProgram) {
        if (isActive && letterCount > 0  && rectfLine != null) {
            for(FilledBubble fb: mBubbles){
                fb.drawLetter(mView, mProjection, mLetterManager,  mTextProgram);
            }
        }
    }
    /*public void debugDraw(Canvas c, CollisionState cs){
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);
        p.setColor(Color.YELLOW);
        RectF bounds = cellBounds.get(cs.cellIndex);
        c.drawRect(bounds.left, bounds.top + rectfLine.top, bounds.right, rectfLine.top+ bounds.bottom, p);
    }*/

    /*-------------------------------------------------------*/
    /*----------------INIT-----------------------------------*/
    /*-------------------------------------------------------*/
    FilledBubble createBubble(int index, int offset){
        FilledBubble fb = new FilledBubble();
        fb.bubble.colorIndex = this.cellColorIndex;
        int curLine = index / Config.BUBBLE_PER_LINE;
        float x =  -Config.getRatioWH() +
                ((float)( (index + offset) % Config.BUBBLE_PER_LINE)*
                        Config.bubbleDiameter - Config.bubbleDiameter/2f) ;
        float y = rectfLine.top - ( (float)curLine * Config.bubbleDiameter + Config.bubbleDiameter / 2f);
        fb.setRowIndex(curLine);
        fb.setPos(x,y);
        return fb;
    }





    /*---------------------------------------------------------*/
    /*------------------UPDATE, MOVE --------------------------*/
    /*---------------------------------------------------------*/
    /***
     * @return true when the line has reached the bottom and should be reassigned
     */
    public int move() {
        return move(isBoosted?Config.POWERUP_FALLING_SPEED:Config.currentSpeed);
    }

    public int move(float dy){
        offsetY(dy);

        if(mBubbles.size() > 0){
            //
            if(rectfLine.centerY() < Config.BOARD_BOTTOM_GLVIEW_POSY){
                return REACH_BOTTOM;
            }
        }

        if(isVanishing){
            vanishCounter++;
            if(vanishCounter >= vanishMax){
                vanishCounter = 0;
                isVanishing = false;
                return VANISH_OVER;
            }
        }
        return NO_EVENT;
    }

    public void offsetY(float offsetY){
        rectfLine.offset(0f, offsetY);
        for(FilledBubble fb: mBubbles){
            fb.bubble.offsetTo(0.0f, offsetY);
        }
    }

    public void updateSpeed(){
        speed[1] = Config.currentSpeed;
    }



    /*------------------------------------------------*/
    /*--------------SETTERS---------------------------*/
    /*------------------------------------------------*/
    //1:Position, speed
    public void setPosF(float topY) {
        rectfLine.offsetTo(0, topY);
        for(FilledBubble fb: mBubbles){
            fb.bubble.setPos(fb.bubble.getX_Gl(),
                    topY - fb.getRowIndex()*Config.bubbleDiameter);
        }
    }
    public void setPosFromBottomF(float bottom) {
        setPosF(bottom + Config.bubbleDiameter*(float)lineCount);
    }
    public void setPos(int y) {
        float newYTop = 1f - ((float)y/Config.height)*2f;
        setPosF(newYTop);
    }

    public void setPosFromBottom(int newBottomY) {
        setPos(newBottomY + (int)Config.bubbleDiameter*Config.height*lineCount);

    }



    //2:Describing Line State
    public void setIsVanishing(boolean state){
        this.isVanishing = state;
    }
    public void setBoost(boolean state){
        isBoosted = state;
        if(state){ speed[0] = 0.0f;
            speed[1] = Config.POWERUP_FALLING_SPEED;
        }
        else{
            speed[0] = 0.0f;
            speed[1] = Config.currentSpeed;
        }
    }
    public void setActive(boolean state){ this.isActive = state; }



    //3: Others, Indexes, contained variables
    public void setInfo(String infoStr){ this.info = infoStr;}
    private void setRandomizedGuessIndexes(String word) {
        Random rand = new Random();
        int numberOfGuess;
        //1 + (int)((Settings.curEmptyRatio-1.0/word.length())* Math.abs(rand.nextInt() % word.length()) );

        numberOfGuess = 1 + (int)Math.ceil(Settings.curEmptyRatio*word.length());
        if(numberOfGuess < 0)numberOfGuess = 0;
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < numberOfGuess; i++) {
            int newIndex;
            int breakLoopCounter = 30;
            do {
                newIndex = Math.abs(rand.nextInt() % word.length());
                --breakLoopCounter;
                if(breakLoopCounter < 0)break;
            } while (newIndex >=0 &&
                    (res.contains(newIndex) ||
                            getContainsExcludedLetters(word.charAt(newIndex))));
            if(breakLoopCounter>=0)res.add(newIndex);
        }

        setGuessValuesFromIndexes(res);
        guessLetterIndexes = res;
    }

    /**
     * @return false if the character can be guess, true if is excluded from guess
     */
    private boolean getContainsExcludedLetters( char charToCheck){
        for(char c: Config.EXCLUDED_LETTERS){
            if(charToCheck == c)return true;
        }
        return false;
    }

    public void setWord(String word, int cellColorIndex) {
        if (cellColorIndex < 0 || cellColorIndex > Config.CELLS_COUNT) {
            this.cellColorIndex = 0;
        } else {
            this.cellColorIndex = cellColorIndex;
        }

        if(word.contains(";")){
            String[] word_tbsource = word.split(";");
            setWord(word_tbsource[0]);
            this.tableSource = Integer.parseInt(word_tbsource[1]);
            this.vocIndex = Integer.parseInt(word_tbsource[2]);
        }else{
            setWord(word);
        }
    }
    /**
     * @param index: Index of the letter to set in the cell
     * @param offset: How many empty cells at left of the line
     */
    private void setLetter(int index, int offset) {
        if (this.guessLetterIndexes.contains(index)) {
                /* Draw in red letters to guess */
            mBubbles.get(index).setLetter('?');
        } else {
            mBubbles.get(index).setLetter(word.charAt(index));
        }
    }



    private void setGuessValuesFromIndexes(ArrayList<Integer> indexes){
        guessLetterValues = new ArrayList<>();
        for(Integer index: indexes){
            guessLetterValues.add(word.charAt(index));
        }
    }
    private void setWord(String word) {
        Log.d("WORD_" , word);
        if (word.length() > MAX_LETTER || word.length() <= 0) {
            letterCount = 0;
            return;
        }
        this.word = word;
        this.letterCount = word.length();
        this.lineCount = 1;

        //Add lines until all the letters are plugged inside
        int lineOverflowCarries = word.length() - Config.BUBBLE_PER_LINE;
        while (lineOverflowCarries > 0) {
            lineOverflowCarries -= Config.BUBBLE_PER_LINE;
            ++lineCount;
            lineOverflowCarries += 1; // add a char for a '-' add the end of a line
        }
        setRandomizedGuessIndexes(word);


        rectfLine = new RectF(-Config.getRatioWH(),
                +Config.bubbleDiameter/2f * (float)(lineCount + 1f),
                Config.getRatioWH(),
                -Config.bubbleDiameter/2f * (float)((float)lineCount + 1f))
        ;
        mBubbles = new ArrayList<>();



        lineOffset = 0;
        if(lineCount == 1) {
            int emptyCells = Config.BUBBLE_PER_LINE - word.length();
            if (emptyCells > 0) {
                Random rand = new Random();
                lineOffset = Math.abs(rand.nextInt() % emptyCells);
            }
        }
        for (int i = 0; i < word.length(); i++) {
            mBubbles.add( createBubble(i, lineOffset));
            setLetter(i, lineOffset);
        }
    }





    /*-----------------------------------------------*/
    /*-------------GETTERS---------------------------*/
    /*-----------------------------------------------*/


    //1:Position, speed
    public float getBottomF(){ return rectfLine.bottom; }
    public float getTopF(){ return rectfLine.top; }

    public int getBottom() {
        return (int) ((1f - rectfLine.bottom)*Config.height/2f);
    }
    public int getTop() {
        if (rectfLine != null)
            return (int) ((1f - rectfLine.top)*Config.height/2f);
        else return -1;
    }

    public float dy(){ return speed[1]; }

    public void collide(int index){
        if (index >= 0 && index < this.word.length() && this.guessLetterIndexes.contains(index)) {
            //char cc = this.word.charAt(index);
            //Log.d("GUESS_INDEXES", this.guessLetterIndexes.toString());
            discover(index);
            //Log.d("GUESS_INDEXES", this.guessLetterIndexes.toString());
        }
    }

    private void discover(int index){
        try {
            this.guessLetterIndexes.remove(this.guessLetterIndexes.indexOf(index));
            this.setLetter(index, lineOffset);
        }catch (Exception e){
            //
        }
    }



    public float[] getCellCenterAt(int index){
        if(mBubbles.size() <= index)return new float[]{ 0.0f, 0.0f};
        return new float[]{mBubbles.get(index).bubble.getRectF().centerX(),
                mBubbles.get(index).bubble.getRectF().centerY()};
    }
    public RectF getCollisionRectF(int index){
        RectF res = new RectF(mBubbles.get(index).bubble.getRectF());
        return res;
    }
    public int getX_pixel(){
        if(mBubbles.size() <= 0 )return -999;
        else{
            return mBubbles.get(0).bubble.getX_pix();
        }
    }

    public int getY_pixel(){
        if(mBubbles.size() <= 0)return -999;
        Log.d("NewY_Bub", "Y  : " +mBubbles.get(0).bubble.getY_pix()) ;
        return mBubbles.get(0).bubble.getY_pix();
    }


    //2:Describing Line State
    public boolean getIsFinished() {
        return this.guessLetterIndexes.size() <= 0;
    }
    public boolean getIsVanishing(){ return isVanishing; }
    public boolean getIsBoosted(){ return this.isBoosted; }
    private boolean getActive(){ return this.isActive; }

    //3: Others, Indexes, contained variables
    public int getColorIndex() {
        return this.cellColorIndex;
    }
    public int getLineCount() {
        return this.lineCount;
    }
    public int getLineIndex(){ return lineIndex; }
    public String getToGuessLetters() {
        String res = "";
        for (int i = 0; i < this.guessLetterIndexes.size(); i++) {
            res += this.word.charAt(this.guessLetterIndexes.get(i));
        }
        return res;
    }
    public ArrayList<Integer> getIndexesOfValueTOGuess(char value){
        if(!this.guessLetterValues.contains(value)){
            return null;
        }else{
            ArrayList<Integer> res = new ArrayList<>();
            for(Integer guessIndex: this.guessLetterIndexes){
                if(this.word.charAt(guessIndex) == value){
                    res.add(guessIndex);
                }
            }
            return res;
        }
    }
    public String getWord() {
        return this.word;
    }
    public int getTableSource(){ return this.tableSource; }
    public String getInfo(){ return this.info; }
    public int getVocIndex(){ return vocIndex;}
    public int getToGuessLetterCount(){ return this.guessLetterIndexes.size();}
    public RectF getRevealedLetter(){
        int randIndex = guessLetterIndexes.get(Math.abs(rand.nextInt(guessLetterIndexes.size())));
        Log.d("Discover" , "randIndex " + randIndex);
        discover(randIndex);
        return mBubbles.get(randIndex).bubble.getRectF();
    }
    public void getRevealedLetters(){
        Object[] indexes = guessLetterIndexes.toArray();
        for(Object index: indexes){
                discover((int)index);
        }
    }

}
