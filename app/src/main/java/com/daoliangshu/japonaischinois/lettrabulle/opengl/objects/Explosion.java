package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.graphics.RectF;
import android.opengl.Matrix;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.MyGLRenderer;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.VertexArray;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Constants;

import java.util.ArrayList;
import java.util.Random;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.ObjectInterface.POSITION_COMPONENT_COUNT;
import static com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.ObjectInterface.STRIDE;
import static com.daoliangshu.japonaischinois.lettrabulle.opengl.objects.ObjectInterface.TEXTURE_COORDINATES_COMPONENT_COUNT;

/**
 * Created by daoliangshu on 2017/7/21.
 */

public class Explosion {
    public final static int EXPLOS_DEFAULT = 0;
    public final static int EXPLOS_CIRCLE = 1;

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

    private int text_code = Constants.CELL2;
    private int explosionType = EXPLOS_DEFAULT;
    private int counter = 0;
    private boolean isActive = false;
    private ArrayList<float[]> particleSpeeds;
    private ArrayList<float[]> particleOffsets;
    private ArrayList<Integer> particleColor;
    private ArrayList<Float> particleAngle;
    private final int CYCLE_COUNT_MAX = 45;

    private float factorSpeed = 1f;
    private float factorWidth = 3f;
    private float explosionGravity = -0.003f * factorSpeed;
    private float factorScale = 0.17f;

    public Explosion() {
        float offsetX = (Config.getRatioWH() / (float) Config.BUBBLE_PER_LINE);
        float offsetY = offsetX;//*Config.getRatioWH();
        initTextureVariables(offsetX, offsetY, 1, 1);
        vertexArray = new VertexArray(VERTEX_DATA);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mTextureMatrix, 0);
        colorIndex = Math.abs(rand.nextInt()) % 4;
    }

    public Explosion(int TEXT_CODE){
        this();
        text_code = TEXT_CODE;
    }

    public void setIsActive(boolean state) {
        if (state) counter = 0;
        isActive = state;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void update() {
        if (isActive) {
            switch(explosionType){
                case EXPLOS_CIRCLE:
                case EXPLOS_DEFAULT:
                    counter++;
                    if (counter >= CYCLE_COUNT_MAX) {
                        isActive = false;
                        particleOffsets = null;
                        particleSpeeds = null;
                    } else {
                        synchronized(Config.lockExplosionUpdate) {
                            for (int i = 0; i < particleOffsets.size(); i++) {
                                particleSpeeds.get(i)[1] += explosionGravity;
                                particleOffsets.get(i)[0] += particleSpeeds.get(i)[0] * factorWidth;
                                particleOffsets.get(i)[1] += particleSpeeds.get(i)[1] * factorSpeed;
                            }
                        }
                    }
                    break;

                default:
            }

        }
    }

    public void initParticles(int numberParticles, float speedMin[], float speedMax[], float initialPos[]) {
        if (numberParticles <= 0 || numberParticles > 100) return;
        particleOffsets = new ArrayList<>();
        particleSpeeds = new ArrayList<>();
        particleColor = new ArrayList<>();
        particleAngle = new ArrayList<>();

        setPos(initialPos[0] + Config.bubbleDiameter/2f, initialPos[1]);
        float rangeX = speedMax[0] - speedMin[0];
        float rangeY = speedMax[1] - speedMin[1];

        for (int i = 0; i < numberParticles; i++) {
            particleOffsets.add(new float[]{0f, 0f});
            particleSpeeds.add(new float[]{speedMin[0] + Math.abs(rand.nextFloat() % rangeX),
                    speedMin[1] + Math.abs(rand.nextFloat() % rangeY)});
            particleColor.add( Math.abs(rand.nextInt())%Constants.colors.length);
            particleAngle.add( Math.abs(rand.nextFloat())%360.0f);
        }
    }

    private void initTextureVariables(float offsetX,
                                      float offsetY,
                                      float textureCellCountX,
                                      float textureCellCountY
    ) {

        baseOffsetX = 1f / (float) TEX_COLUMN_COUNT;
        baseOffsetY = 1f / (float) TEX_ROW_COUNT;

        mRectF = new RectF();
        mRectF.set(-offsetX,
                offsetY,
                offsetX,
                -offsetY);

        VERTEX_DATA = new float[]{
                0.0f, 0.0f, baseOffsetX * textureCellCountX / 2f, baseOffsetY * textureCellCountY / 2f,
                -offsetX, -offsetY, 0f, baseOffsetY,
                offsetX, -offsetY, baseOffsetX * textureCellCountX, baseOffsetY * textureCellCountY,
                offsetX, offsetY, baseOffsetX * textureCellCountX, 0.0f,
                -offsetX, offsetY, 0f, 0.0f,
                -offsetX, -offsetY, 0f, baseOffsetY * textureCellCountY
        };

        VERTEX_DATA2 = new float[]{
                0.0f, 0.0f, baseOffsetX * 2f, baseOffsetY * 2f,
                -offsetX, -offsetY, 0f, baseOffsetY * 4f,
                offsetX, -offsetY, baseOffsetX * 4f, baseOffsetY * 4f,
                offsetX, offsetY, baseOffsetX * 4f, 0.0f,
                -offsetX, offsetY, 0f, 0.0f,
                -offsetX, -offsetY, 0f, baseOffsetY * 4f
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




    float scratch[] = new float[16];

    public void draw(
            float[] mViewMatrix,
            float[] mProjectionMatrix,
            TextureShaderProgram mTextProgram
    ) {
        draw(mViewMatrix, mProjectionMatrix, mTextProgram, 1f - (float)counter/(float)CYCLE_COUNT_MAX);
    }

    public void draw(
            float[] mViewMatrix,
            float[] mProjectionMatrix,
            TextureShaderProgram mTextProgram,
            float transparency) {

        Matrix.multiplyMM(scratch, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, scratch, 0);

        //Matrix.multiplyMM(scratch, 0, this.mModelMatrix, 0, mMVPMatrix, 0);
        // Draw the table.
        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, MyGLRenderer.drawableTexture, this.getTextCoordinates(), transparency);
        this.bindData(mTextProgram);
        for (int i =0; i< this.particleOffsets.size(); i++){
            mTextProgram.setOffset(particleOffsets.get(i));
            mTextProgram.setColorModifier(Constants.colors[particleColor.get(i)]);
            this.draw();
        }

    }


    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


    public void move(float dx, float dy) {
        offsetTo(dx, dy);
    }

    public void setPos(float x, float topY) {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, x - Config.bubbleDiameter/2f, topY, 0f);
        Matrix.scaleM(mModelMatrix, 0, factorScale, factorScale, 1.0f);
        mRectF.offsetTo(x, topY);
    }

    public void offsetTo(float dx, float dy) {
        Matrix.translateM(mModelMatrix, 0, dx, dy, 0f);
        mRectF.offset(dx, dy);
    }


    public float getX_Gl() {
        return mModelMatrix[12];
    }

    public float getY_Gl() {
        return mModelMatrix[13];
    }


    public float[] getTextCoordinates() {
        if (Config.useAlternativeCells) {
            return new float[]{
                    Constants.TEXTURE_COORDINATES[text_code * 2],
                    Constants.TEXTURE_COORDINATES[text_code * 2 + 1]
            };
        }
        return new float[]{Constants.TEXTURE_COORDINATES[colorIndex * 2], Constants.TEXTURE_COORDINATES[colorIndex * 2 + 1]};
    }
}
