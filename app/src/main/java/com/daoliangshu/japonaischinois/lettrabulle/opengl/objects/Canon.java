package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

/**
 * Created by daoliangshu on 2017/7/7.
 */

import android.graphics.RectF;
import android.opengl.Matrix;
import android.util.Log;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.VertexArray;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by daoliangshu on 2017/7/5.
 */
public class Canon implements ObjectInterface{

    public float angle = 0;
    public final static int CANON_TEXTURE_ID = Constants.CELL_CANON;

    public float[] mModelMatrix = new float[16];
    public float[] mTextureMatrix = new float[16];

    private RectF mRectF;

    private static float[] VERTEX_DATA, VERTEX_DATA2;

    private final VertexArray vertexArray, vertexArray2;

    private float widht_Gl, height_Gl;
    private float baseFromCanonTranslation;

    public Canon() {
        baseFromCanonTranslation = -0.15f;

        initVertexData();
        vertexArray = new VertexArray(VERTEX_DATA);
        vertexArray2 = new  VertexArray(VERTEX_DATA2);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mTextureMatrix, 0);
    }

    /**
     * Initialize the vertex data {X,Y,S,T}
     */


    private void initVertexData(){
        float baseOffsetX = Constants.BASE_OFFSET_X;
        float baseOffsetY = Constants.BASE_OFFSET_Y;


        float locationOffsetX = 0.20f;

        float locationOffsetY = locationOffsetX;
        float ratio = Config.getRatioWH();

        float screenWidth = (float)Config.width;
        float screenHeight = (float)Config.height;

        widht_Gl = locationOffsetX*2f;
        height_Gl = locationOffsetY*2f;

        mRectF = new RectF();
        mRectF.set(-locationOffsetX,
                locationOffsetY,
                locationOffsetX,
                -locationOffsetY);



        VERTEX_DATA = new float[]{
                0.0f, 0.0f, baseOffsetX*2f, baseOffsetY*2f,
                -locationOffsetX, -locationOffsetY, 0f, baseOffsetY*4f,
                locationOffsetX, -locationOffsetY, baseOffsetX*4f, baseOffsetY*4f,
                locationOffsetX, locationOffsetY, baseOffsetX*4f, 0.0f,
                -locationOffsetX, locationOffsetY, 0f, 0.0f,
                -locationOffsetX, -locationOffsetY, 0f, baseOffsetY*4f
        };

        VERTEX_DATA2 = new float[]{
                0.0f, 0.0f, baseOffsetX*2f, baseOffsetY*2f,
                -locationOffsetX, -locationOffsetY, 0f, baseOffsetY*5f,
                locationOffsetX, -locationOffsetY, baseOffsetX*4f, baseOffsetY*5f,
                locationOffsetX, locationOffsetY, baseOffsetX*4f, 0.0f,
                -locationOffsetX, locationOffsetY, 0f, 0.0f,
                -locationOffsetX, -locationOffsetY, 0f, baseOffsetY*5f
        };
    }


    /**
     * Bind Data before drawing
     * @param textureProgram
     */
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
        Matrix.setIdentityM(mRotatedProjection, 0);
        Matrix.translateM(mRotatedProjection, 0, mModelMatrix, 0, 0.0f, baseFromCanonTranslation, 0f);
        Matrix.multiplyMM(mRotatedProjection, 0, mViewMatrix, 0, mRotatedProjection, 0);
        Matrix.multiplyMM(scratch, 0, mProjectionMatrix , 0, mRotatedProjection, 0);

        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, drawableTexture, this.getTextCoordinates(Constants.CELL_CANON_BASE));
        this.bindData2(mTextProgram);
        this.draw();


        float[] sc = new float[16];
        Matrix.setIdentityM(sc, 0);
        //Matrix.scaleM(sc, 0, 1f , Config.getRatioWH(), 1f);
        Matrix.rotateM(scratch, 0, sc, 0,  angle, 0f, 0f, 1f);
        Matrix.multiplyMM(scratch, 0, mModelMatrix, 0, scratch, 0);
        //draw canon
        Matrix.setIdentityM(mRotatedProjection, 0);


        Log.d("ANGLE", "angle : " + angle);
        Matrix.multiplyMM(mRotatedProjection, 0, mViewMatrix, 0, scratch, 0);

        Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, mRotatedProjection, 0);

        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, drawableTexture, this.getTextCoordinates());
        this.bindData(mTextProgram);
        this.draw();
    }




    public void move(float dx, float dy){
        Matrix.translateM(mModelMatrix, 0, dx, dy, 0.0f);
        if(getY() < -1 + 0.125f)mModelMatrix[13] = 1f;;
    }

    private float prevAngle = 0f;

    public boolean setAngle(float angle){
        if(angle > -90 && angle < 90) {
            this.angle = angle;
            return true;
        }
        return false; // if event is not in canon range
    }

    public void setPos(float x, float y){
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, x, y, 0f);

        float newLeft = x - mRectF.width()/2f ;
        float newTop =  y - mRectF.height()/2f;
        mRectF.offsetTo(newLeft, newTop);

    }


    public float getX() {
        return mModelMatrix[12];
    }

    public void setX(float x){
        mModelMatrix[12] = x;
    }

    public float getY(){
        return mModelMatrix[13];
    }

    public String getPosAsString() {
        String res = "";
        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) res += ")\n( ";
            res += "_"+mModelMatrix[i];

        }
        return res;
    }

    public float getCanonCenterX(){
        return mRectF.centerX();
    }

    public float getCanonCenterY(){
        return mRectF.centerY();
    }

    /**
     *
     * @return : The coordonates in the texture
     */
    public float[] getTextCoordinates(){
        return new float[]{Constants.TEXTURE_COORDINATES[CANON_TEXTURE_ID*2],
                            Constants.TEXTURE_COORDINATES[CANON_TEXTURE_ID*2 + 1]};
    }

    public float[] getTextCoordinates(int text_id){
        return new float[]{Constants.TEXTURE_COORDINATES[text_id*2],
                Constants.TEXTURE_COORDINATES[text_id*2 + 1]};
    }


    public int getY_pix(){
        return (int) mRectF.centerY();
    }

    public int getX_pix(){
        return (int)mRectF.centerX();
    }

    public float getX_Gl() {
        return mModelMatrix[12];
    }

    public float getY_Gl(){
        return mModelMatrix[13];
    }

    public float getWidth_Gl(){ return widht_Gl; }
    public  float getHeight_Gl(){ return height_Gl; }
    public float getBaseFromCanonTranslation(){ return baseFromCanonTranslation; }
}
