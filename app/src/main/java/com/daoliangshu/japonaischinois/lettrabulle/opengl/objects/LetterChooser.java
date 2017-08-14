package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.graphics.RectF;
import android.opengl.Matrix;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.VertexArray;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.LetterManager;

import java.util.ArrayList;
import java.util.Random;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by daoliangshu on 2017/7/8.
 */

public class LetterChooser  implements ObjectInterface{
    public  static Random rand = new Random();
    boolean contentLock = false;
    public float angle = 9f;
    public static int TEXTURE_ID = Constants.CELL_LETTER_CHOOSER;
    private float[] VERTEX_DATA2;

    private RectF mRectF;
    private float screenWidth = Config.width;
    private float screenHeight = Config.height;

    public float[] mModelMatrix = new float[16];
    private int selectedIndex = 4;
    private ArrayList<Character> cellContents;
    private final VertexArray vertexArray2;


    private FilledBubble[] bubbleChoices;
    private Background chooserBackground;

    private float componentWidth = 0.9f * Config.getRatioWH();
    private float panelRadius = componentWidth - componentWidth/8f;
    private int previousIndex = -1;


    public LetterChooser(){
        initVertexData();
        vertexArray2 = new VertexArray(VERTEX_DATA2);
        Matrix.setIdentityM( mModelMatrix, 0);

        bubbleChoices = new FilledBubble[5];
       for(int i=0; i< 5; i++){
           bubbleChoices[i] = new FilledBubble();
           float angle = 180 - 9 - i*18;
           float tmpX = (float)Math.cos(Math.toRadians(angle))*panelRadius;
           float tmpY = (float)Math.sin(Math.toRadians(angle))*panelRadius;
           bubbleChoices[i].setPos(tmpX - Config.bubbleDiameter/2f,
                   tmpY + Config.bubbleDiameter/2f);
       }

       chooserBackground = new Background((componentWidth/2f), (componentWidth/2f), Constants.CELL_CHOOSER_BG);
        chooserBackground.setPos((componentWidth/2f), (componentWidth/2f));

    }


    private void initVertexData(){
        float baseOffsetX = Constants.BASE_OFFSET_X;
        float baseOffsetY = Constants.BASE_OFFSET_Y;

        float locationOffsetX = (componentWidth/2f);
        float locationOffsetY = (componentWidth/2f)/Config.getRatioWH();


        mRectF = new RectF();
        mRectF.set(-locationOffsetX,
                    locationOffsetY,
                    locationOffsetX,
                    -locationOffsetY);



        VERTEX_DATA2 = new float[]{
                0.0f, 0.0f, baseOffsetX*2f, baseOffsetY*2f,
                -locationOffsetX, -locationOffsetY, 0f, baseOffsetY*4f,
                locationOffsetX, -locationOffsetY, baseOffsetX*4f, baseOffsetY*4f,
                locationOffsetX, locationOffsetY, baseOffsetX*4f, 0.0f,
                -locationOffsetX, locationOffsetY, 0f, 0.0f,
                -locationOffsetX, -locationOffsetY, 0f, baseOffsetY*4f
        };
    }


    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray2.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray2.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }


    public void setPosF(float x, float y){
        float dx = x - mModelMatrix[12];
        float dy = y - mModelMatrix[13];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, x, y, 0f);

        float ratio = screenWidth / screenHeight;

        float newLeft = x - mRectF.width()/2f;
        float newTop =  y - mRectF.height()/2f;
        mRectF.offsetTo(newLeft, newTop);
        for(FilledBubble fb: bubbleChoices){
            fb.bubble.offsetTo(dx, dy);
        }

        chooserBackground.setPos(x - componentWidth/2f, y + (componentWidth/2f)*Config.getRatioWH() );
    }

    public void setPos(float x, float y) {
        setPosF(x, y);
    }



    public void drawLetters(float[] mViewMatrix,
                            float[] mProjection,
                            LetterManager mLetterManager,
                            TextureShaderProgram mTextProgram){
        for(FilledBubble fb: bubbleChoices){
            fb.drawLetter(mViewMatrix,
                            mProjection,
                            mLetterManager,
                            mTextProgram);
        }
    }


    public void drawBubbles(
                            float[] mViewMatrix,
                            float[] mProjectionMatrix,
                            TextureShaderProgram textProgram){
        for(FilledBubble fb: bubbleChoices){
            fb.bubble.draw(
                            mViewMatrix,
                            mProjectionMatrix,
                            textProgram);
        }

    }

    public char getLetter(){ return this.bubbleChoices[selectedIndex].getLetter(); }


    public void drawBackground(
                            float[] mViewMatrix,
                            float[] mProjectionMatrix,
                            TextureShaderProgram textProgram){
        chooserBackground.draw(
                                mViewMatrix,
                                mProjectionMatrix,
                                textProgram);
    }





    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }



    private float[] mRotatedProjection = new float[16];
    private float[] scratch = new float[16];


    public void draw(
                     float[] mViewMatrix,
                     float[] mProjectionMatrix,
                     TextureShaderProgram mTextProgram,
                     int drawableTexture){
        //draw base

        Matrix.rotateM(mRotatedProjection, 0, mModelMatrix, 0, this.angle + 180f, 0f, 0f, 1f);
        Matrix.multiplyMM(mRotatedProjection,0,mViewMatrix, 0, mRotatedProjection, 0);
        Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, mRotatedProjection, 0);
        // Draw the table.
        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, drawableTexture, this.getTextCoordinates());
        this.bindData(mTextProgram);
        this.draw();
    }

    public float[] getTextCoordinates(){
        return new float[]{Constants.TEXTURE_COORDINATES[TEXTURE_ID*2],
                Constants.TEXTURE_COORDINATES[TEXTURE_ID*2 + 1]};
    }

    public float getCanonCenterX(){
        return mRectF.centerX();
    }

    public float getCanonCenterY(){
        return mRectF.centerY();
    }



    public char setAngle(float angle, float moveX, float moveY){
        moveX = -Config.getRatioWH() + (moveX/Config.height)*2f;
        moveY = 1f -( moveY/Config.height)*2f;
        float dy = mRectF.centerY()  - moveY;
        float dx = mRectF.centerX()  - moveX;
        float d = (float)Math.sqrt(
                dy*dy + dx*dx
        );
        if(d > panelRadius + panelRadius/8f)return getLetter();
        return setAngle(angle);
    }

    public char setAngle(float angle){
        previousIndex = selectedIndex;
        for(int i = 0; i<5; i++){
            if(angle <90  - (i)*18){
                this.angle = 90 + 9 - (i+1)*18;
                selectedIndex = i;
            }
        }
        return this.bubbleChoices[selectedIndex].getLetter();
    }

    public int getIndexFromAngle(float angle) {
        int index = 0;
        for (int i = 0; i < 5; i++) {
            if (angle >= 180 - (i + 1) * 18) {
                index = i;
            } else {
                break;
            }
        }
        return index;
    }



    public float getX_Gl() {
        return mModelMatrix[12];
    }

    public float getY_Gl(){
        return mModelMatrix[13];
    }


    public int getY_pix(){
        return (int) mRectF.centerY();

    }

    public int getX_pix(){
        return (int)mRectF.centerX();
    }

    public void setNewFiveLengthLetterSet(String fiveLengthString) {
        contentLock = true;
        if (fiveLengthString == null || fiveLengthString.length() < 5) return;
        int[] toShuffle = {0, 1, 2, 3, 4};
        LetterChooser.shuffle(toShuffle);

        for (int i = 0; i < 5; i++) {
            bubbleChoices[i].setLetter(fiveLengthString.charAt(toShuffle[i]));
        }
        contentLock = false;
    }

    public static <T> void shuffle(int[] arr) {
        if (rand == null) {
            rand = new Random();
        }

        for (int i = arr.length - 1; i > 0; i--) {
            swap(arr, i, rand.nextInt(i + 1));
        }
    }

    public static <T> void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

}
