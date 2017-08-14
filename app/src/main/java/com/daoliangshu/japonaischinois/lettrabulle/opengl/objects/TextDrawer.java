package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.opengl.Matrix;
import android.util.Log;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.MyGLRenderer;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.Config;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.LetterManager;

/**
 * Created by daoliangshu on 2017/7/24.
 */

public class TextDrawer {
    private float[] mModelMatrix, scratch;
    private LetterManager lm;
    private TextureShaderProgram program;
    public TextDrawer(LetterManager lm, TextureShaderProgram program){
        this.lm = lm;
        this.program = program;
        mModelMatrix = new float[16];
        scratch = new float[16];
    }
    private float textSize = 1f; //default: 32px
    private float verticalSpacing = 0.01f;
    private float horizontalSpacing = 0.01f;
    private String text;

    public void setString(String str){ this.text = text;}
    public String getString(){return this.text; }

    public void setVerticalSpacing(float space){ this.verticalSpacing = space; }
    public float getVerticalSpacing(){ return this.verticalSpacing; }
    public void setHorizontalSpacing(float space){ this.horizontalSpacing = space; }
    public float getHorizontalSpacing(){ return this.horizontalSpacing; }

    public void setScale(float scale){ this.textSize = scale; };
    public float getScale(float scale){ return this.textSize; };



    public void drawText(float[] mView,
                         float[] mProjection,
                         float left,
                         float top,
                         String text,
                         TextureShaderProgram program
                         ){
        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.translateM(mModelMatrix, 0, left, top , 0f);
        Matrix.scaleM(mModelMatrix, 0, this.textSize/2f, this.textSize/2f, 0f);
        float curWidth, prevCurWidht = 0.0f;
        program.useProgram();
        int lineCount = 0;
        for(int i=0; i< text.length(); i++){
            if((curWidth = lm.getLetterWidth(text.charAt(i))) > 0.0f){
                Log.d("WIDTH_LETTER", "Previous width is : " + prevCurWidht);
                Matrix.translateM(mModelMatrix,
                        0,
                        (prevCurWidht + curWidth)/(2f*Config.getRatioWH()) +
                                horizontalSpacing,
                        0f,
                        0f);
                Matrix.multiplyMM(scratch, 0, mView, 0, mModelMatrix, 0);
                Matrix.multiplyMM(scratch, 0, mProjection, 0, scratch, 0);
                program.setUniforms(scratch,
                                    MyGLRenderer.letterTexture,
                                    lm.getTextureCoordinates(text.charAt(i)));
                lm.mModelMatrix = mModelMatrix;
                lm.bindData(program);
                lm.draw();
                prevCurWidht = curWidth;
            }else if(text.charAt(i) == '\n'){
                lineCount += 1;
                Matrix.setIdentityM(mModelMatrix,0);
                Matrix.translateM(mModelMatrix, 0, left, top , 0f);
                Matrix.scaleM(mModelMatrix, 0, this.textSize, this.textSize, 0f);
                Matrix.translateM(mModelMatrix,
                        0,
                        0,
                        -textSize*lm.getHeight() - this.verticalSpacing,
                        0f);
            }
        }
    }
}
