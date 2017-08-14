package com.daoliangshu.japonaischinois.lettrabulle.opengl.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.Matrix;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.structures.VertexArray;

import java.util.ArrayList;
import java.util.Random;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by daoliangshu on 2017/7/7.
 */
public class LetterManager {
    private static final int LETTER_WIDTH = 64;
    private static final int LETTER_HEIGHT = 64;

    private static final float SIZE_DEFAULT = 0.125f;


    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT +
            TEXTURE_COORDINATES_COMPONENT_COUNT) *
            4;



    //Texture components informations:
    public static int columnCount;
    public static int rowCount;
    public static ArrayList<Character> indexedLetters;
    public static ArrayList<Float> letterWidths;



    public float[] mModelMatrix = new float[16];
    public float[] mTextureMatrix = new float[16];
    public static final Random rand = new Random();

    private static float[] VERTEX_DATA;


    private final VertexArray vertexArray;

    public static Bitmap image;
    private float baseOffsetX;
    private float baseOffsetY;
    public static String letterSet;

    public LetterManager(String letterSet) {


        LetterManager.letterSet = letterSet;
        //first create a bitmap englobing the set of characters used.
        image = initBitmap(letterSet);
        float offsetX = (float)(1f/((float)columnCount));
        float offsetY = (float)(1f/((float)rowCount));
        float centerY = (float)(1f/((float)rowCount));
        //letter frame is larger, to conveniently draw out of bound letters
        VERTEX_DATA = new float[]{
                0.0f,  0.0f, centerY/2, centerY/2,
                -0.125f, -0.125f, 0f, offsetY,
                0.125f, -0.125f, offsetX, offsetY,
                0.125f, 0.125f, offsetX, 0.0f,
                -0.125f, 0.125f, 0f, 0.0f,
                -0.125f, -0.125f, 0f, offsetY
        };

        baseOffsetX = offsetX;
        baseOffsetY = offsetY;

        vertexArray = new VertexArray(VERTEX_DATA);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mTextureMatrix, 0);


    }

    public Bitmap initBitmap(String letterSet){
        Paint p = new Paint();
        int count = letterSet.length();
        columnCount = (int)Math.ceil(Math.sqrt(count));
        rowCount =columnCount;

        Bitmap bm = Bitmap.createBitmap(LETTER_WIDTH*columnCount,
                                        LETTER_HEIGHT*rowCount,
                                        Bitmap.Config.ARGB_8888);
       // bm.eraseColor(getRe);
        Canvas canvas = new Canvas(bm);
        bm.eraseColor(Color.TRANSPARENT);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        //canvas.drawRect(new Rect(0,0,LETTER_WIDTH*columnCount,LETTER_HEIGHT*rowCount),
        //        p
        //        )
        //;
        p.setColor(Color.BLACK);
        p.setTextSize(32f);
        indexedLetters = new ArrayList<>();
        letterWidths = new ArrayList<>();

        float widths[] = new float[letterSet.length()];
        p.getTextWidths(letterSet, widths);

        for(int i=0; i<rowCount; i++){
            for(int j=0; j<columnCount; j++){
                if(i*columnCount + j >=  count)break;
                letterWidths.add(new Float(2f*widths[i*columnCount + j]/Config.width));
                indexedLetters.add(letterSet.charAt(i*columnCount + j));
                canvas.drawText(""+letterSet.charAt(i*columnCount + j),
                                j*LETTER_WIDTH + LETTER_WIDTH/2 - widths[i*columnCount + j]/2,
                        (i)*LETTER_HEIGHT + (LETTER_HEIGHT*3)/4 ,
                                p);
            }
        }

        return bm;
    }

    public void bindData(final TextureShaderProgram textureProgram) {
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

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


    public void move(float dx, float dy){
        Matrix.translateM(mModelMatrix, 0, dx, dy, 0.0f);
        if(getY() < -1 + 0.125f)mModelMatrix[13] = 1f;

    }

    public void setPos(float x, float y){
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, x, y, 0f);
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

    public float[] getTextureCoordinates(char letter){

        if(indexedLetters.contains(letter)) {
            int index = indexedLetters.indexOf(letter);
            float[] res = {
                    (float)((int)(index%columnCount)*baseOffsetX),
                    (float)((int)(index/columnCount)*baseOffsetY)
            };
            return res;
        }else{
            return new float[]{0.0f, 0.0f};
        }
    }

    public float getLetterWidth(char c){
        if(!indexedLetters.contains(c))return -1f;
        return letterWidths.get(indexedLetters.indexOf(c));
    }

    public float getHeight(){ return this.SIZE_DEFAULT; }


}

