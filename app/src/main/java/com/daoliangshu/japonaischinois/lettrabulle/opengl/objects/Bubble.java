package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.graphics.RectF;
import android.opengl.Matrix;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.MyGLRenderer;
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
 * Created by daoliangshu on 2017/7/5.
 */
public class Bubble implements ObjectInterface{

    private int bubbleCode = Constants.CELL_BLUE;


    public static ArrayList<float[]> textureCoordinates;
    public static ArrayList<float[]> textureScales;


    public static final int TEX_COLUMN_COUNT = 32;
    public static final int TEX_ROW_COUNT = 8;
    public static final int TEX_UNIT_WIDTH = 64;
    public static final int TEX_UNIT_HEIGHT = 64;
    public static float baseOffsetX;
    public static float baseOffsetY;


    public float[] mModelMatrix = new float[16];
    public float[] mTextureMatrix = new float[16];
    public int colorIndex = 0;
    public static final Random rand = new Random();
    private RectF mRectF;

    private static float[] VERTEX_DATA;
    private static float[] VERTEX_DATA2;

    private final VertexArray vertexArray;
    private final VertexArray vertexArray2;


    public Bubble() {
        this((Config.getRatioWH()/(float)Config.BUBBLE_PER_LINE),
                (Config.getRatioWH()/(float)Config.BUBBLE_PER_LINE));
    }

    public Bubble( float wf, float hf){
        initTextureVariables(wf, hf,  1,1);
        vertexArray = new VertexArray(VERTEX_DATA);
        vertexArray2 = new VertexArray(VERTEX_DATA2);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mTextureMatrix, 0);
        colorIndex =  Math.abs(rand.nextInt())%4;
    }

    private void initTextureVariables(float offsetX,
                                      float offsetY,
                                      float textureCellCountX,
                                      float textureCellCountY
                                      ){


            baseOffsetX = 1f/(float)TEX_COLUMN_COUNT;
            baseOffsetY = 1f/(float)TEX_ROW_COUNT;

        mRectF = new RectF();
        mRectF.set(-offsetX,
                offsetY,
                offsetX,
                -offsetY);

            VERTEX_DATA = new float[]{
                    0.0f, 0.0f, baseOffsetX*textureCellCountX/2f, baseOffsetY*textureCellCountY/2f,
                    -offsetX, -offsetY, 0f, baseOffsetY,
                    offsetX, -offsetY, baseOffsetX*textureCellCountX, baseOffsetY*textureCellCountY,
                    offsetX, offsetY, baseOffsetX*textureCellCountX, 0.0f,
                    -offsetX, offsetY, 0f, 0.0f,
                    -offsetX, -offsetY, 0f, baseOffsetY*textureCellCountY
            };

        VERTEX_DATA2 = new float[]{
                0.0f, 0.0f, baseOffsetX*2f, baseOffsetY*2f,
                -offsetX, -offsetY, 0f, baseOffsetY*4f,
                offsetX, -offsetY, baseOffsetX*4f, baseOffsetY*4f,
                offsetX, offsetY, baseOffsetX*4f, 0.0f,
                -offsetX, offsetY, 0f, 0.0f,
                -offsetX, -offsetY, 0f, baseOffsetY*4f
        };
    }



    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

       vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    //test (different) texture size sample
    public void bindData2(TextureShaderProgram textureProgram) {
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



    float scratch[] = new float[16];

    public void draw(
            float[] mViewMatrix,
            float[] mProjectionMatrix,
            TextureShaderProgram mTextProgram
            ){
        draw(mViewMatrix, mProjectionMatrix, mTextProgram, 1f);
    }

    public void draw(
                     float[] mViewMatrix,
                     float[] mProjectionMatrix,
                     TextureShaderProgram mTextProgram,
                     float transparency){

        Matrix.multiplyMM(scratch, 0, mViewMatrix, 0, this.mModelMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, scratch, 0);

        //Matrix.multiplyMM(scratch, 0, this.mModelMatrix, 0, mMVPMatrix, 0);
        // Draw the table.
        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, MyGLRenderer.drawableTexture, this.getTextCoordinates(), transparency);
        if(this.colorIndex != 4)
            this.bindData(mTextProgram);
        else this.bindData2(mTextProgram);
        this.draw();
    }


    public void draw() {
       glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


    public void move(float dx, float dy){
        offsetTo(dx, dy);
    }

    public void setPos(float x, float topY){
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, x + Config.bubbleDiameter/2f, topY - Config.bubbleDiameter/2f, 0f);
        mRectF.offsetTo(x, topY);
    }

    public void offsetTo(float dx, float dy){
        Matrix.translateM(mModelMatrix, 0, dx, dy, 0f);
        mRectF.offset(dx, dy);
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


    public String getPosAsString() {
        String res = "";
        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) res += ")\n( ";
            res += "_"+mModelMatrix[i];
        }
        return res;
    }


    public float[] getTextCoordinates(){
        switch(bubbleCode){
            case Constants.CELL_BTN_POWERUP:
            case Constants.CELL_BTN_QUIT:
            case Constants.CELL_BTN_DELETE_VOC:
                return new float[]{Constants.TEXTURE_COORDINATES[bubbleCode*2] , Constants.TEXTURE_COORDINATES[bubbleCode*2+1]};
            default:
                if(Config.useAlternativeCells){
                    return new float[]{
                            Constants.TEXTURE_COORDINATES[Constants.CELL2*2],
                            Constants.TEXTURE_COORDINATES[Constants.CELL2*2 + 1]
                    };
                }
                return new float[]{Constants.TEXTURE_COORDINATES[colorIndex*2] , Constants.TEXTURE_COORDINATES[colorIndex*2+1]};
        }

    }

    private int counter = 0;
    private char current = 'a';
    public char updateLetter(){
        char letter = 'a';
        if(counter++ > 40){
            counter = 0;
            letter = LetterManager.letterSet.charAt(Math.abs(rand.nextInt())% LetterManager.letterSet.length());
            current = letter;
        }else{
            letter = current;
        }
        return letter;
    }

    public RectF getRectF(){ return this.mRectF; }

    public void setColorIndex(int colorIndex){
        this.colorIndex = colorIndex;
    }
    public void setBubbleCode(int bubbleCode){ this.bubbleCode = bubbleCode; }

}
