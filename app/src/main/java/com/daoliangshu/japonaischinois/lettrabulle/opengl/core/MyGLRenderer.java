package com.daoliangshu.japonaischinois.lettrabulle.opengl.core;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.daoliangshu.japonaischinois.R;
import com.daoliangshu.japonaischinois.core.data.Settings;
import com.daoliangshu.japonaischinois.lettrabulle.ProjectileState;
import com.daoliangshu.japonaischinois.lettrabulle.manager.CollisionManager;
import com.daoliangshu.japonaischinois.lettrabulle.manager.CollisionState;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.Avatar;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.Background;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleButton;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLine;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.BubbleLineInterface;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.Canon;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.Explosion;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.LetterChooser;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.Projectile;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.TextDrawer;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.LetterManager;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.TextureHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

/**
 * Created by daoliangshu on 2017/7/5.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
    public boolean gameState;
    private boolean needLetterChooserUpdate = false;

    ArrayList<HashMap<String, String>> mVocList;
    public MyGLSurfaceView mView;
    public Canon mCanon;
    public Background mBackground;
    public LetterChooser mLetterChooser;
    //public BubbleLine bbl;
    public Projectile mProjectile;
    public BubbleButton mButtons[];
    public TextDrawer mTextDrawer;

    public Avatar mAvatar;
    public Explosion mExplosion[];
    private Context context;

    public static int drawableTexture, letterTexture;
    private LetterManager mLetterManager;
    private TextureShaderProgram mTextProgram;

    public CollisionManager cm;
    public ProjectileState currentProjState;

    public ArrayList<BubbleLineInterface> wordLines;

    public MyGLRenderer(Context context) {
        this.context = context;
    }


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mTextProgram = new TextureShaderProgram(context);
        drawableTexture = TextureHelper.loadTexture(context, R.drawable.tiles1);
        letterTexture = TextureHelper.loadTexture(context, mLetterManager.image);
    }

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] scratch = new float[16];


    //-----------------------------------------------------------------//
    //---------------ON_DRAW_FRAME-------------------------------------//
    //-----------------------------------------------------------------//
    private float smoothedDeltaRealTime_ms = 17.5f;
    private float movAverageDeltaTime_ms = smoothedDeltaRealTime_ms;
    private long lastRealTimeMeasurement_ms; //store last time measuremnt

    private static final float movAveragePeriod = 40;
    private static final float smoothFactor = 0.1f;

    public void onDrawFrame(GL10 gl) {
        updateGame(gl, smoothedDeltaRealTime_ms);
        drawGame(gl);

        // Moving average calc
        long currTimePick_ms = SystemClock.uptimeMillis();
        float realTimeElapsed_ms;
        if (lastRealTimeMeasurement_ms > 0) {
            realTimeElapsed_ms = (currTimePick_ms - lastRealTimeMeasurement_ms);
        } else {
            realTimeElapsed_ms = smoothedDeltaRealTime_ms; // just the first time
        }
        movAverageDeltaTime_ms = (realTimeElapsed_ms + movAverageDeltaTime_ms * (movAveragePeriod - 1)) / movAveragePeriod;

        // Calc a better aproximation for smooth stepTime
        smoothedDeltaRealTime_ms = smoothedDeltaRealTime_ms + (movAverageDeltaTime_ms - smoothedDeltaRealTime_ms) * smoothFactor;
        lastRealTimeMeasurement_ms = currTimePick_ms;
    }


    public void updateGame(GL10 gl, float smoothDeltaRealTime_ms) {
        switch(Config.gameState){
            case Constants.GAME_STATE_ACTIVE:
                if(needLetterChooserUpdate && mView.setNewFiveLengthLetterSet()){
                    needLetterChooserUpdate = false;
                };
                mBackground.update();
                updateProjectile();
                mAvatar.update();
                moveLines();
                mExplosion[0].update();
                mExplosion[1].update();
                CollisionState cs = cm.update();
                if(cs != null){
                    updateCollideLine(cs);
                }
                break;
        }
    }

    private void updateProjectile(){
        switch(mProjectile.update()){
            case Projectile.UPDATE_RESETING:
                mProjectile.setLetter(mLetterChooser.getLetter());
                break;
            case Projectile.UPDATE_WALL_COLLISION:
                mView.recheckCollision();
                break;
            default:
                break;
        }
    }

    private void lineVanish(BubbleLine bbl){
        synchronized (Config.lock1){
            bbl.getRevealedLetters();
            bbl.setIsVanishing(true);
            bbl.setInfo(mView.getInfo(bbl.getVocIndex(), bbl.getTableSource()));
            bbl.setActive(false);
            cm.removeLineByIndex(bbl.getLineIndex());
            mView.rootActivity.computeAndAddScore(Config.SCORE_UNIT,
                    Config.CURRENT_SCORE_WEIGHT *
                            (1 + Math.abs(1 - bbl.getBottom() / Config.height)));
            mView.recomputeTopDownOrderedLineState(false);
            bbl.setActive(true);
        }

    }



    private void updateCollideLine(CollisionState cs){
        BubbleLineInterface bbl;
        bbl = wordLines.get(cs.lineIndex);
        bbl.collide(cs.cellIndex);
        mExplosion[0].initParticles(15,
                                new float[]{ -0.011f, 0.031f},
                                new float[]{ 0.011f, 0.071f},
                ((BubbleLine)bbl).getCellCenterAt(cs.cellIndex));
        mExplosion[0].setIsActive(true);
        if(bbl.getIsFinished()) {
            lineVanish((BubbleLine)bbl);
        }
        mProjectile.reset();
        mProjectile.setLetter(mLetterChooser.getLetter());

    }

    public void setView(MyGLSurfaceView mView){
        this.mView = mView;
        mView.setNewFiveLengthLetterSet();
    }



    public void drawGame(GL10 gl) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        mTextProgram.setGroup(Constants.GROUP_BACKGROUND_COMPONENT);
        mTextProgram.setColorModifier(Constants.colors[0]);
        mTextProgram.setAngleRadian(mBackground.getAngleRadian());
        drawBackground(mBackground);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (mLetterManager != null) {
            mTextProgram.setGroup(Constants.GROUP_BUBBLELINE_COMPONENT);
            mLetterChooser.drawBackground(mViewMatrix, mProjectionMatrix, mTextProgram);
            if(mProjectile.getIsAcceptingCollision()) {
                mTextProgram.setColorModifier(Constants.colors[2]);
                mTextProgram.updateCenterOfLight(mProjectile.getCenterXF(), mProjectile.getCenterYF());
                mProjectile.draw(mViewMatrix, mProjectionMatrix, mTextProgram);
            }
            mTextProgram.setColorModifier(Constants.colors[0]);
            mTextProgram.setGroup(Constants.GROUP_INTERFACE_COMPONENT);
            mCanon.draw(mViewMatrix, mProjectionMatrix, mTextProgram, drawableTexture);
            if(!mProjectile.getIsAcceptingCollision()) {
                mTextProgram.setGroup(Constants.GROUP_BUBBLELINE_COMPONENT);
                mTextProgram.setColorModifier(Constants.colors[2]);
                mTextProgram.updateCenterOfLight(mProjectile.getCenterXF(), mProjectile.getCenterYF());
                mProjectile.draw(mViewMatrix, mProjectionMatrix, mTextProgram);
            }
            mTextProgram.setColorModifier(Constants.colors[0]);
            mTextProgram.setGroup(Constants.GROUP_INTERFACE_COMPONENT);
            mLetterChooser.draw(mViewMatrix, mProjectionMatrix, mTextProgram, drawableTexture);
            mTextProgram.setGroup(Constants.GROUP_BUBBLELINE_COMPONENT);
            mLetterChooser.drawBubbles(
                    mViewMatrix,
                    mProjectionMatrix,
                    mTextProgram);
           // mTextProgram.setGroup(Constants.GROUP_BOARD_COMPONENT);

                if(wordLines != null){
                    synchronized (Config.lock1){
                        mTextProgram.setGroup(Constants.GROUP_BUBBLELINE_COMPONENT);
                        for (BubbleLineInterface bli : wordLines) {
                            mTextProgram.setColorModifier(Constants.colors[bli.getColorIndex()]);
                            ((BubbleLine) bli).draw(mViewMatrix, mProjectionMatrix, mTextProgram);
                            if(bli.getIsVanishing() && bli.getInfo() != null){
                                mTextDrawer.drawText(mViewMatrix,
                                        mProjectionMatrix,
                                        0f,
                                        ((BubbleLine) bli).getBottomF(),
                                        bli.getInfo(),
                                        mTextProgram);
                            }
                        }
                    }
            }
        }
        mTextProgram.setGroup(Constants.GROUP_INTERFACE_COMPONENT);
        mTextProgram.setColorModifier(Constants.colors[0]);
        mAvatar.draw(mViewMatrix, mProjectionMatrix, mTextProgram);
        if(mLetterChooser != null){
            mLetterChooser.drawLetters(mViewMatrix, mProjectionMatrix, mLetterManager, mTextProgram);
        }
        if(mLetterManager != null){
            synchronized (Config.lock1) {
                for (BubbleLineInterface bli : wordLines) {
                    ((BubbleLine) bli).drawLetters(mViewMatrix, mProjectionMatrix, mLetterManager, mTextProgram);
                }
            }
        }
        if(!mProjectile.getIsDrawnLetter()){
            mProjectile.mBubble.drawLetter(mViewMatrix, mProjectionMatrix, mLetterManager, mTextProgram);
        }
        for(Explosion expl: mExplosion){
            if(expl.getIsActive()){
                mTextProgram.setColorModifier(Constants.colors[0]);
                mTextProgram.setGroup(Constants.GROUP_EFFECT_COMPONENT);
                synchronized (Config.lockExplosionUpdate) {
                    expl.draw(mViewMatrix, mProjectionMatrix, mTextProgram);

                }
            }
        }



        mTextProgram.setGroup(Constants.GROUP_INTERFACE_COMPONENT);
        mTextProgram.setColorModifier(Constants.colors[0]);
        for(int i=0; i< mButtons.length; i++){
            mButtons[i].draw(mViewMatrix, mProjectionMatrix, mTextProgram);
        }
        mTextProgram.setGroup(Constants.GROUP_INTERFACE_COMPONENT);
        mTextProgram.setColorModifier(Constants.colors[0]);
        //mAvatar.draw(mViewMatrix, mProjectionMatrix, mTextProgram);
        Matrix.setIdentityM(scratch, 0);
        glDisable(GL_BLEND);
    }

    public void setNeedLetterChooserUpdate(boolean state){
        needLetterChooserUpdate = state;
    }

    public void drawBackground(Background bg) {
        // Draw the table.
        mTextProgram.useProgram();
        bg.draw(mViewMatrix, mProjectionMatrix, mTextProgram);
    }



    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.d("UPD_SURFACE", "onSizeChange_Renderer");
        GLES20.glViewport(0, 0, width, height);
        Matrix.frustumM(mProjectionMatrix, 0, -Config.getRatioWH(), Config.getRatioWH(), -1, 1, 5, 7);

        mCanon = new Canon();
        mCanon.setPos(0.0f, -1f + mCanon.getHeight_Gl() / 2f - mCanon.getBaseFromCanonTranslation());

        mBackground = new Background(width, height, Constants.CELL_BG);
        mBackground.setPos(0.0f, 0.0f);


        mLetterChooser = new LetterChooser();
        mLetterChooser.setPos(Config.getRatioWH(), -1f);

        mProjectile = new Projectile(mCanon.getX_Gl(), mCanon.getY_Gl());

        mAvatar = new Avatar();
        mAvatar.setPos(-Config.getRatioWH() + 0.8f* Config.bubbleDiameter, -1.0f   + mAvatar.getHeight_Gl());

        cm = new CollisionManager();

        mView.setNewFiveLengthLetterSet();
        mProjectile.setLetter(mLetterChooser.getLetter());
        mExplosion = new Explosion[2];
        mExplosion[0] = new Explosion();
        mExplosion[1] = new Explosion(Constants.CELL_STAR);

        mButtons = new BubbleButton[3];
        //Exit button
        mButtons[0] = new BubbleButton(-Config.getRatioWH(),
                -1f + 1.2f*Config.bubbleDiameter,
                Constants.CELL_BTN_QUIT);
        //Delete entry from set of lines
        mButtons[1] = new BubbleButton(-Config.getRatioWH()/2f - 1.2f*Config.bubbleDiameter/2f,
                -1f + 10f*Config.bubbleDiameter/4f,
                Constants.CELL_BTN_POWERUP);
        //Boost
        mButtons[2] = new BubbleButton(-Config.getRatioWH()/2f + 1.2f*Config.bubbleDiameter/4f,
                                    -1  + 5f*Config.bubbleDiameter/4f,
                Constants.CELL_BTN_DELETE_VOC);
    }



    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }


    private void boostUpdate(){
        // Lines are locked outside the method
        while(mView.topDownOrderedLineState.size() != wordLines.size()){}
        float bottomLineTopPos = ((BubbleLine)wordLines.get(mView.topDownOrderedLineState.
                get(mView.topDownOrderedLineState.size() -1)[0].intValue())).getTopF();
        boolean isBoosted = wordLines.get(0).getIsBoosted();
        Log.d("Boosted!?", "topTop : " + bottomLineTopPos);
        if(!isBoosted && bottomLineTopPos > 1f) {
            Log.d("Boosted" , "TRUE");
            for (BubbleLineInterface bli : wordLines) {
                bli.setBoost(true);
            }
            mView.recheckCollision();
        }else if(isBoosted && bottomLineTopPos < 1f){
            Log.d("Boosted" , "FALSE");
            for (BubbleLineInterface bli : wordLines) {
                bli.setBoost(false);
            }
            mView.recheckCollision();
        }
    }

    public void resetLinePositions(){
        float offset = Config.board_top - Config.board_bottom;
        synchronized (Config.lock1){
            for(BubbleLineInterface bbl: wordLines){
                ((BubbleLine)bbl).offsetY(offset);
            }
        }
    }

    public void moveLines() {
        ArrayList<Integer> linesToUpdate = new ArrayList<>();
        int reachedEndLineIndex = -1;
        synchronized (Config.lock1){
            boostUpdate();
            for (BubbleLineInterface bli : wordLines) {
                switch(bli.move()){
                    case BubbleLine.REACH_BOTTOM:
                        linesToUpdate.add((bli).getLineIndex());
                        Config.gameState = Constants.GAME_STATE_OVER;
                        mView.rootActivity.pauseGame();
                        break;

                    case BubbleLine.VANISH_OVER:
                        int colorIndex = mView.getRandColorIndex(mView.getTopLineIndex());
                        bli.setWord(Settings.dbEntryManager.getRandomWord(), colorIndex);
                        mView.repositionNeededLineAtTopStack(bli.getLineIndex());
                        mView.recomputeTopDownOrderedLineState(true);
                        mView.setNewFiveLengthLetterSet();

                        break;
                }
            }

            if(linesToUpdate.size() > 0 && mView != null){
                for(Integer value: linesToUpdate){
                    mView.recomputeTopDownOrderedLineState(false);
                    wordLines.
                            get(value).
                            setWord(Settings.dbEntryManager.getRandomWord(),
                                    mView.getRandColorIndex(
                                            wordLines.
                                                    get(value).getColorIndex()));
                    ((BubbleLine)wordLines.get(value)).setPosFromBottomF(
                            mView.getTopLineTopPosF() + 0.00001f
                    );
                }
                mView.recomputeTopDownOrderedLineState(true);
            }
        }

    }


    /**
     * Initialize the letters used in Gl
     * A letter can be display if it is not first declared here
     * @param mVocList : the list of vocabulary from which to extract letters
     */
    public void setLetters(ArrayList<HashMap<String, String>> mVocList) {
        this.mVocList = mVocList;
        //predefined letter
        String ls = "?abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPKRSTUVWXYZ ,./<>:'[]";
        if (mVocList == null) return;
        for (HashMap<String, String> hm : mVocList) {
            if(hm == null)continue;
            for (Map.Entry<String, String> hs : hm.entrySet()) {
                if(hs.getValue() == null) continue;
                for (int i = 0; i < hs.getValue().length(); i++) {
                    if (!ls.contains(("" + hs.getValue().charAt(i)))) {
                        ls += hs.getValue().charAt(i);
                    }
                }
            }
        }
        mLetterManager = new LetterManager(ls);
        mTextDrawer = new TextDrawer(mLetterManager, mTextProgram);
    }

    public int checkButtons(float x, float y,final int BTN_MODE){
        int res = -1;
        for(int i=0; i< mButtons.length; i++){
            if(mButtons[i].isPressed(x, y, BTN_MODE)){
                res = i;
            }
        }
        return res;
    }

    public int finishLine(int index){
        if(wordLines.get(index).getIsVanishing())return -1;
        lineVanish((BubbleLine)wordLines.get(index));
        return wordLines.get(index).getVocIndex();
    }

    public void retrieveLetterAnimate(RectF rect){
        mExplosion[1].initParticles(15,
                new float[]{ -0.011f, 0.031f},
                new float[]{ 0.011f, 0.071f},
                new float[]{ rect.centerX(), rect.centerY()});
        mExplosion[1].setIsActive(true);
    }
}
