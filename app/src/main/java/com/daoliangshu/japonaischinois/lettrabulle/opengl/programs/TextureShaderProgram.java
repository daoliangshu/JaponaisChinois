package com.daoliangshu.japonaischinois.lettrabulle.opengl.programs;

import android.content.Context;

import com.daoliangshu.japonaischinois.R;

import static android.opengl.GLES10.GL_TEXTURE0;
import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2f;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by daoliangshu on 2017/7/6.
 */

public class TextureShaderProgram extends ShaderProgram{


    // Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int uTextureCoordinatesLocation;
    private final int uTransparency;
    private final int uCenterOfLightLocation;
    private final int uGroup;
    private final int uFragGroup;
    private final int uColor;
    private final int uAngleRadian;
    private final int uOffset;

    private float[] centerOfLightPosition = { 0f, 5f};
    private float rangeOfLight = 0.1f;



    private final int aTextureCoordinatesLocation;

    // Attribute locations
    private final int aPositionLocation;
   // private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader,
                R.raw.texture_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        uTextureCoordinatesLocation = glGetUniformLocation(program, "u_TextureCoordinates");
        uTransparency = glGetUniformLocation(program, "u_Transparency");
        uCenterOfLightLocation = glGetUniformLocation(program, "u_CenterOfLightLocation");
        //uRangeOfLight = glGetUniformLocation(program, "u_RangeOfLight");
        uAngleRadian = glGetUniformLocation(program, "u_AngleRadian");
        uGroup = glGetUniformLocation(program, "u_GroupComponent");
        uFragGroup = glGetUniformLocation(program, "u_FragmentGroup");
        uOffset = glGetUniformLocation(program, "u_OffsetLocation");
        uColor = glGetUniformLocation(program, "u_Color");


        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        aTextureCoordinatesLocation =
                glGetAttribLocation(program, A_TEXTURE_COORDINATES);



    }

    public void setOffset(float[] offset2f){
        glUniform2f(uOffset, offset2f[0], offset2f[1]);
    }

    public void setUniforms(float[] matrix, int textureId, float[] textCoords){
        setUniforms(matrix, textureId, textCoords, 1f);
    }


    public void setAngleRadian(float angleRadian){
        glUniform1f(uAngleRadian, angleRadian);
    }

    public void updateCenterOfLight( float x, float y){
            centerOfLightPosition[0] = x;
            centerOfLightPosition[1] = y;
    }

    public void setGroup(int group){
        glUniform1i(uGroup, group);
        glUniform1i(uFragGroup, group);
    }

    public void setColorModifier(float[] colors3f){
        if(colors3f.length != 3)return;
        glUniform3f(uColor,colors3f[0], colors3f[1], colors3f[2]);
    }


    public void setUniforms(float[] matrix, int textureId, float[] textCoords, float transparency) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform2f(uTextureCoordinatesLocation, textCoords[0], textCoords[1]); //update texture coordinates
        glUniform1f(uTransparency, transparency);
        glUniform2f(uCenterOfLightLocation, centerOfLightPosition[0], centerOfLightPosition[1]);
        //glVertexAttrib2f(aTextureCoordinatesLocation, 0.5f, 0.0f);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);
    }




    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

   public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
   }

}
