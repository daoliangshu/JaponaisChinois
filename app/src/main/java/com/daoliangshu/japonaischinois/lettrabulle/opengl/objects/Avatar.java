package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.graphics.RectF;
import android.opengl.Matrix;
import android.util.Log;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.MyGLRenderer;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.VertexArray;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;

import java.util.ArrayList;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by daoliangshu on 2017/7/14.
 */
public class Avatar implements ObjectInterface {
    public static ArrayList<float[]> textureCoordinates;
    public static float baseOffsetX;
    public static float baseOffsetY;
    public float[] mModelMatrix = new float[16];
    private RectF mRectF; //convertion in  pixel of the components bounds ( temporary)
    private static float[] VERTEX_DATA;
    private final VertexArray vertexArray;

    private int texture_id[] = {
            Constants.CELL_AVATAR_IDLE,
            Constants.CELL_AVATAR_ACTIVE
    };

    private boolean isPushing = false;
    private int pushingCount = 18;
    private final int pushingLimit = 18;


    private float[] scratch = new float[16];

    public Avatar(){
         float offsetX = 0.11f;
         float offsetY = 0.10f;
        initTextureVariables(offsetX, offsetY, 4f, 4f);
        vertexArray = new VertexArray(VERTEX_DATA);
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    /*-------------------------------------------------------*/
    /*----------------------INIT-----------------------------*/
    /*-------------------------------------------------------*/
    private void initTextureVariables(
            float offsetX,
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
                -offsetX, -offsetY, 0f, baseOffsetY*textureCellCountY,
                offsetX, -offsetY, baseOffsetX*textureCellCountX, baseOffsetY*textureCellCountY,
                offsetX, offsetY, baseOffsetX*textureCellCountX, 0.0f,
                -offsetX, offsetY, 0f, 0.0f,
                -offsetX, -offsetY, 0f, baseOffsetY*textureCellCountY
        };
    }



    /*-------------------------------------------------------*/
    /*----------------------BINDING--------------------------*/
    /*-------------------------------------------------------*/

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


    /*-------------------------------------------------------*/
    /*----------------------DRAW-----------------------------*/
    /*-------------------------------------------------------*/
    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
    public void draw(float[] mView, float[] mProjection, TextureShaderProgram mTextProgram){
        Matrix.multiplyMM(scratch, 0, mView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mProjection, 0, scratch, 0);
        // Draw the table.
        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, MyGLRenderer.drawableTexture, this.getTextCoordinates());
        this.bindData(mTextProgram);
        this.draw();
    }

    /*-------------------------------------------------------*/
    /*----------------------UPDATE---------------------------*/
    /*-------------------------------------------------------*/
    public void update(){
        if(isPushing) {
            new Thread() {
                @Override
                public void run() {
                    --pushingCount;
                    if (pushingCount <= 0) {
                        setIsPushing(false);
                        isPushing = false;
                        pushingCount = pushingLimit;
                    }
                }
            }.start();
        }
    }
    public void offset(float dx, float dy){
        Matrix.translateM(mModelMatrix, 0, dx, dy, 0f);
        mRectF.offset(dx, dy);
    }
    /*-------------------------------------------------------*/
    /*----------------------SETTERS--------------------------*/
    /*-------------------------------------------------------*/
    //Position
    public void setPos(float x, float y){
        Matrix.setIdentityM(mModelMatrix, 0);
        Log.d("AvatarPos", "x " + (x + mRectF.width()/2f) + "    y " + (y + mRectF.height()/2f));
        Matrix.translateM(mModelMatrix, 0, x + mRectF.width()/2f, y + mRectF.height()/2f, 0f);
        mRectF.offsetTo(x, y);
    }
    //State
    public void setIsPushing(boolean isPushing) {
        this.isPushing = isPushing;
    }




    /*-------------------------------------------------------*/
    /*----------------------GETTERS--------------------------*/
    /*-------------------------------------------------------*/
    //Position
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
    public float[] getTextCoordinates(){
        if(isPushing){
            return new float[]{Constants.TEXTURE_COORDINATES[texture_id[1] *2],
                    Constants.TEXTURE_COORDINATES[texture_id[1] *2 + 1]};
        }else{
            return new float[]{Constants.TEXTURE_COORDINATES[texture_id[0] *2],
                    Constants.TEXTURE_COORDINATES[texture_id[0] *2 + 1]};
        }
    }

    public float getHeight_Gl(){ return Math.abs(mRectF.height()); }
    public float getWidth_Gl(){ return Math.abs(mRectF.width()); }

    //State
    public boolean getIsPushing(){ return this.isPushing; }

}
