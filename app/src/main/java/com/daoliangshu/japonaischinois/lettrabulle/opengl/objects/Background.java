package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.opengl.Matrix;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.MyGLRenderer;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.VertexArray;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Background of the game
 * Created by daoliangshu on 2017/7/8.
 */

public class Background  implements ObjectInterface{


    private int texture_id = Constants.CELL_BG;
    private float[] VERTEX_DATA;
    private float angleRadian = 0.0f;


    private float[] mModelMatrix = new float[16];

    private final VertexArray vertexArray;

    public Background(float width, float height, int texture_id){
        this.texture_id = texture_id;
        switch(texture_id){
            case Constants.CELL_BG:
                initVertexData(1f, 1f, 5, 8 );
                break;
            case Constants.CELL_CHOOSER_BG:
                initVertexData(width, height, 8, 8);
                break;

        }

        vertexArray = new VertexArray(VERTEX_DATA);
        Matrix.setIdentityM( mModelMatrix, 0);
    }


    private void initVertexData(float offsetX,
                                float offsetY,
                                float textColumnCellCount,
                                float textRowCellCount){
        float baseOffsetX = Constants.BASE_OFFSET_X;
        float baseOffsetY = Constants.BASE_OFFSET_Y;


        //(float)Math.abs(Math.cos(Math.toRadians(45) - Math.toRadians(angle)));



        VERTEX_DATA = new float[]{
                0.0f, 0.0f, baseOffsetX*textColumnCellCount/2f, baseOffsetY*textRowCellCount/2f,
                -offsetX, -offsetY, 0f, baseOffsetY*textRowCellCount,
                offsetX, -offsetY, baseOffsetX*textColumnCellCount, baseOffsetY*textRowCellCount,
                offsetX, offsetY, baseOffsetX*textColumnCellCount, 0.0f,
                -offsetX, offsetY, 0f, 0.0f,
                -offsetX, -offsetY, 0f, baseOffsetY*textRowCellCount
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


    private float[] scratch = new float[16];

    public void draw(float[] mViewMatrix,
                     float[] mProjectionMatrix,
                     TextureShaderProgram mTextProgram){
        Matrix.multiplyMM(scratch, 0, mViewMatrix, 0, this.mModelMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mProjectionMatrix,0,  scratch, 0);

        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, MyGLRenderer.drawableTexture, this.getTextCoordinates());
        this.bindData(mTextProgram);
        this.draw();
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

    public float[] getTextCoordinates(){
        return new float[]{Constants.TEXTURE_COORDINATES[texture_id *2],
                Constants.TEXTURE_COORDINATES[texture_id *2 + 1]};
    }

    public void setPos(float x, float y){
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix,0,  x, y, 0f);
    }



    public float getX_Gl() {
        return mModelMatrix[12];
    }

    public float getY_Gl(){
        return mModelMatrix[13];
    }


    public int getY_pix(){
        //return (int) mRectF.centerY();
        return 0;
    }

    public int getX_pix(){
        //return (int)mRectF.centerX();
        return 0;
    }


    public float getAngleRadian(){ return this.angleRadian; }

    public void update(){
        angleRadian += 0.01f;
        if(angleRadian > Math.PI * 2f){ angleRadian -= Math.PI * 2f; }
    }

}
