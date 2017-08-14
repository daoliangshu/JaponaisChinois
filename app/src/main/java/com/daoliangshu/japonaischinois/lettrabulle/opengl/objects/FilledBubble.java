package com.daoliangshu.japonaischinois.lettrabulle.opengl.objects;

import android.opengl.Matrix;

import com.daoliangshu.japonaischinois.lettrabulle.opengl.core.MyGLRenderer;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.programs.TextureShaderProgram;
import com.daoliangshu.japonaischinois.lettrabulle.opengl.util.LetterManager;

/**
 * Created by daoliangshu on 2017/7/10.
 * Representation of a bubble as well as the letter inside
 */

public class FilledBubble {
    public int indexRowInLine = 0;
    public Bubble bubble;
    private char currentLetter = 'a';

    public FilledBubble(){
        bubble = new Bubble();
    }

    public void setLetter(char letter){ this.currentLetter = letter; }
    public char getLetter(){ return this.currentLetter; }

    public void setPos(float x, float y){
        bubble.setPos(x, y);
    }

    float[] scratch = new float[16];




    public void drawLetter(float[] mView,
                           float[] mProjection,
                           LetterManager mLetterManager,
                           TextureShaderProgram mTextProgram){
        mLetterManager.mModelMatrix = bubble.mModelMatrix;
        Matrix.multiplyMM(scratch, 0, mView, 0, bubble.mModelMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mProjection, 0, scratch, 0);
        // Draw the table.
        mTextProgram.useProgram();
        mTextProgram.setUniforms(scratch, MyGLRenderer.letterTexture, mLetterManager.getTextureCoordinates(getLetter()) );
        mLetterManager.bindData(mTextProgram);

        mLetterManager.draw();
    }

    public void setRowIndex(int index){ this.indexRowInLine = index; }
    public int getRowIndex(){ return this.indexRowInLine; }


}
