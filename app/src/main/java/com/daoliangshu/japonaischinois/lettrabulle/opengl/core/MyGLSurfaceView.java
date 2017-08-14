package com.daoliangshu.japonaischinois.lettrabulle.opengl.core;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.daoliangshu.japonaischinois.core.Settings;
import com.daoliangshu.japonaischinois.lettrabulle.ProjectileState;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleButton;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLine;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLineInterface;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.Canon;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.LetterChooser;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.FloatComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by daoliangshu on 2017/7/5.
 */
class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    public ArrayList<Float[]> topDownOrderedLineState;
    private final Random rand;

    public int mWidth = -1;
    public int mHeight = -1;
    public int letter_chooser_left_bound = -1;
    public int board_bottom_posy = -1;

    public OpenGLES20Activity rootActivity;

    /*----------------------CONSTRUCTOR------------------------------*/
    public MyGLSurfaceView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        rand = new Random();
        mRenderer = new MyGLRenderer(context);
        mRenderer.setView(this);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /*---------------------------------------------------------------*/
    /*----------------------EVENTS-----------------------------------*/
    /*---------------------------------------------------------------*/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Config.width = w;
        Config.height = h;
        mWidth = w;
        mHeight = h;
        mRenderer.setLetters(Settings.entryManager.getVocList());
        board_bottom_posy = (int) (mHeight * (1f - Config.BOARD_BOTTOM_GLVIEW_POSY) / 2f);
        letter_chooser_left_bound = mWidth - (int) (mWidth * Config.LETTER_CHOOSER_WIDTH_RATIOX);

        Config.bubbleDiameter = (Config.getRatioWH() * 2f) / Config.BUBBLE_PER_LINE;
        initLines();
        recomputeTopDownOrderedLineState(true);
        setNewFiveLengthLetterSet();
        Config.gameState = Constants.GAME_STATE_ACTIVE;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            float moveX = event.getX(i);
            float moveY = event.getY(i);
            float moveX_Gl = -Config.getRatioWH() + 2f * moveX / Config.height;
            float moveY_Gl = 1 - 2f * moveY / Config.height;
            int motionAction = event.getAction();
            switch (motionAction & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                    mRenderer.checkButtons(moveX_Gl, moveY_Gl, BubbleButton.BTN_DOWN);
                case MotionEvent.ACTION_MOVE:
                    if (moveY < board_bottom_posy) {
                        mRenderer.mCanon.setAngle(getCanonAngle(mRenderer.mCanon, moveX, moveY));
                    }
                    if (moveY > board_bottom_posy &&
                            moveX > letter_chooser_left_bound) {
                        //Move inside Letter Chooser area
                        char letter = mRenderer.mLetterChooser.setAngle(getCanonAngle(mRenderer.mLetterChooser, moveX, moveY),
                                moveX,
                                moveY);
                        mRenderer.mProjectile.setLetter(letter);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (moveY < board_bottom_posy &&
                            mRenderer.mProjectile.getIsMoving() &&
                            mRenderer.mCanon.setAngle(
                                    getCanonAngle(mRenderer.mCanon,
                                            moveX,
                                            moveY))) {
                        setNewFiveLengthLetterSet();
                        mRenderer.mProjectile.activate(mRenderer.mCanon.angle);
                        mRenderer.mAvatar.setIsPushing(true);
                        mRenderer.mProjectile.setLetter(mRenderer.mLetterChooser.getLetter());

                        synchronized (Config.lock1) {
                            //Vectorial collision check
                            mRenderer.cm.processAll(new ProjectileState(mRenderer.mProjectile.getProjectileState()),
                                    mRenderer.wordLines);
                        }

                    }

                    int res = mRenderer.checkButtons(moveX_Gl, moveY_Gl, BubbleButton.BTN_UP);
                    switch (res) {
                        case 0:
                            rootActivity.finish();
                            break;
                        case 2: // Remove the current entry from the line set
                            getEraseBottomLineAndVoc();
                            break;
                        case 1: //setBoost
                            getDiscoverOneLetter();
                            break;
                        case -1:
                    }
                    break;
            }
        }
        return true;
    }

    /*---------------------------------------------------------------*/
    /*----------------------INIT,RESET-------------------------------*/
    /*---------------------------------------------------------------*/
    private void initLines() {
        synchronized (Config.lock1) {
            mRenderer.wordLines = new ArrayList<>();
            int numWordLine = 6;
            int prevCellColorIndex = -1;
            for (int vocIndex = 0; vocIndex < numWordLine; vocIndex++) {
                prevCellColorIndex = getRandColorIndex(prevCellColorIndex);
                mRenderer.wordLines.add(new BubbleLine(vocIndex));
                String randWord = Settings.entryManager.getRandomWord();
                mRenderer.wordLines.get(vocIndex).setWord(randWord, prevCellColorIndex); //here
                if (vocIndex == 0)
                    ((BubbleLine) mRenderer.wordLines.get(vocIndex)).setPosF(1f + Config.bubbleDiameter / 2f);
                else {
                    ((BubbleLine) mRenderer.wordLines.get(vocIndex)).setPosFromBottomF(((BubbleLine) mRenderer.wordLines.get(vocIndex - 1)).getTopF() + 0.000001f);
                }
            }
        }
    }
    public void resetLinePositions() {
        mRenderer.resetLinePositions();
    }

    /*---------------------------------------------------------------*/
    /*----------------------UPDATE-----------------------------------*/
    /*---------------------------------------------------------------*/
    public void updateSpeed() {
        synchronized (Config.lock1) {
            for (BubbleLineInterface bl : mRenderer.wordLines) {
                bl.updateSpeed();
            }
        }
    }
    public synchronized void repositionNeededLineAtTopStack(int index) {
        int offset = 0;
        if (this.topDownOrderedLineState.get(0)[0].intValue() == index) {
            offset = 1;
        }
        ((BubbleLine) mRenderer.wordLines.get(index)).
                setPosFromBottomF(
                        ((BubbleLine) mRenderer.wordLines.
                                get(this.topDownOrderedLineState.
                                        get(offset)[0].intValue())).getTopF() + 0.000001f);

    }
    public synchronized void recomputeTopDownOrderedLineState(boolean needSetCurrent) {
        while (Config.lockTopDownUpdate) {
        }
        Config.lockTopDownUpdate = true;
        topDownOrderedLineState = new ArrayList<>();
        for (int i = 0; i < mRenderer.wordLines.size(); i++) {
            Float[] values = {(float) i, ((BubbleLine) mRenderer.wordLines.get(i)).getTopF()};
            topDownOrderedLineState.add(values);
        }

        Collections.sort(topDownOrderedLineState, new FloatComparator());
            /* Update infoPanel information when order has changed */

        if (needSetCurrent && topDownOrderedLineState.size() > 0) {
            setCurrent();
        }
        Config.lockTopDownUpdate = false;
    }
    public void recheckCollision() {
        //Lines are locked outside the method
        if (mRenderer.mProjectile.getIsAcceptingCollision()) {
            //redo vectorial collision check
            mRenderer.currentProjState = mRenderer.mProjectile.getProjectileState();
            mRenderer.cm.processAll(mRenderer.currentProjState,
                        mRenderer.wordLines);
        }
    }

    /*---------------------------------------------------------------*/
    /*----------------------SETTERS-----------------------------------*/
    /*---------------------------------------------------------------*/
    //Position
    public void setCurrent() {

        int dstWord = mRenderer.wordLines.get(getBottomLineIndex()).getVocIndex();
        rootActivity.setCurrent(dstWord);
    }
    //State
    //Other
    public boolean setNewFiveLengthLetterSet() {
        if (mRenderer.mLetterChooser == null || topDownOrderedLineState == null) {
            mRenderer.setNeedLetterChooserUpdate(true);
            return false;
        }
        mRenderer.mLetterChooser.setNewFiveLengthLetterSet(getFiveLengthLetterSet());
        return true;
    }
    public void setActivity(OpenGLES20Activity ra) {
        this.rootActivity = ra;
    }

    /*---------------------------------------------------------------*/
    /*----------------------GETTERS-----------------------------------*/
    /*---------------------------------------------------------------*/
    //Position, angle
    public float getTopLineTopPosF() {
        return ((BubbleLine) mRenderer.wordLines.get(getTopLineIndex())).getTopF();
    }
    public float getBottomLineTopPosF() {
        return ((BubbleLine) mRenderer.wordLines.get(getBottomLineIndex())).getTopF();
    }
    private float getCanonAngle(Object obj, float moveX, float moveY) {
        moveX = -Config.getRatioWH() + (moveX / Config.height) * 2f;
        moveY = 1f - (moveY / Config.height) * 2f;
        float dx, dy;
        if (obj instanceof Canon) {
            dx = ((Canon) obj).getCanonCenterX() - moveX;
            dy = ((Canon) obj).getCanonCenterY() - moveY;
        } else if (obj instanceof LetterChooser) {
            dx = ((LetterChooser) obj).getCanonCenterX() - moveX;
            dy = ((LetterChooser) obj).getCanonCenterY() - moveY;
        } else return 0f;

        float tmpAngle = (float) Math.toDegrees(Math.abs(Math.atan(dy / dx)));
        if (dy > 0f) tmpAngle = -tmpAngle;
        if (dx < 0f) tmpAngle = 180f - tmpAngle;
        tmpAngle *= -1f;
        tmpAngle += 90f;
        return tmpAngle;
    }
    //State
    //Others
    public int getTopLineIndex() {
        return topDownOrderedLineState.get(0)[0].intValue();
    }
    public int getBottomLineIndex() {
        return topDownOrderedLineState.get(topDownOrderedLineState.size() - 1)[0].intValue();
    }
    public String getInfo(int vocIndex, int tableSource) {
        return rootActivity.mEntryManager.getInfo(vocIndex, tableSource);
    }

    /**
     * Retrieves five letters amongst the unknowns letters
     * @return String of five letters
     */
    public String getFiveLengthLetterSet() {
        String tmpLetterSet = "";
        synchronized (Config.lock1) {
            if (topDownOrderedLineState.size() > 0) {
                //From down to top
                for (int i = topDownOrderedLineState.size() - 1; i >= 0; i--) {
                    tmpLetterSet += mRenderer.wordLines.get(this.topDownOrderedLineState.get(i)[0].intValue()).getToGuessLetters();
                }
            }
            String letterSet = "";
            for (int i = 0; i < tmpLetterSet.length(); i++) {
                if (!letterSet.contains("" + tmpLetterSet.charAt(i))) {
                    letterSet += tmpLetterSet.charAt(i);
                }
                if (letterSet.length() >= 5) break;
            }
            while (letterSet.length() < 5) letterSet += '~';
            return letterSet;
        }
    }
    public int getRandColorIndex(int prevCellColorIndex) {
        int cellColorIndex = Math.abs(rand.nextInt()) % Config.CELLS_COUNT;
        if (cellColorIndex == prevCellColorIndex) {
            cellColorIndex = (cellColorIndex + 1 +
                    Math.abs(rand.nextInt()) % (Config.CELLS_COUNT - 2)) % Config.CELLS_COUNT;
        }
        return cellColorIndex;
    }
    public void getEraseBottomLineAndVoc(){
        int index = topDownOrderedLineState.get(topDownOrderedLineState.size()-1)[0].intValue();
        int vocIndex = vocIndex = mRenderer.finishLine(index);
        if( vocIndex != -1){
            rootActivity.mEntryManager.removeVoc(vocIndex);
        }
    }
    public void getDiscoverOneLetter(){
        int offset = 0;
        int count = topDownOrderedLineState.size();
        if(count < 1)return;
        int seuil = 2;
        synchronized (Config.lock1){
            while(((BubbleLine)mRenderer.
                    wordLines.
                    get(topDownOrderedLineState.
                            get(count -1 - offset)[0].intValue())
                ).getToGuessLetterCount() < seuil){
                offset += 1;
                //Try to fetch a line that has at least the number of letter to guess
                // indicated by variable 'seuil'
                if(count - 1 - offset < 0){
                    offset = 0;
                    seuil -=1;
                }
                if(seuil == 0)return;
            }

            int lineToDiscoverIndex =topDownOrderedLineState.get(count -1 - offset)[0].intValue();
            int guessCount = ((BubbleLine)mRenderer.wordLines.get(lineToDiscoverIndex)).getToGuessLetterCount();
            Log.d("Discover", "index: " + lineToDiscoverIndex + "   with " + guessCount + " guess(es)");
            RectF res = ((BubbleLine)mRenderer.wordLines.get(lineToDiscoverIndex)).getRevealedLetter();
            if(res != null)mRenderer.retrieveLetterAnimate(res);
            if(((BubbleLine)mRenderer.wordLines.get(lineToDiscoverIndex)).getToGuessLetterCount() == 0){
                //Last guess has been revealed, need vanish
                mRenderer.finishLine(lineToDiscoverIndex);
            }
        }
    }
}