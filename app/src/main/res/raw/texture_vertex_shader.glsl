
uniform mat4 u_Matrix;
attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;
uniform vec2 u_TextureCoordinates;
uniform vec2 u_CenterOfLightLocation;
uniform float u_AngleRadian;
uniform int u_GroupComponent;
uniform vec2 u_OffsetLocation; // for explosion
varying vec2 v_TextureCoordinates;
varying vec3 v_Light;
varying vec3 v_Normal;
varying vec3 v_Position;


void main()
{

    v_TextureCoordinates = vec2(a_TextureCoordinates+u_TextureCoordinates);
   v_Light = vec3(u_Matrix * vec4(u_CenterOfLightLocation , 0.0 , 0.0));
   v_Normal = vec3(u_Matrix * vec4(0.0, 1.0, 0.0, 0.0));
        //
       if(u_GroupComponent == 2){
       //bg
        v_Position = vec3(u_Matrix * vec4(a_Position[0] + 0.05 * sin(u_AngleRadian), a_Position[1], a_Position[2], a_Position[3]));
        gl_Position = u_Matrix * vec4(a_Position[0] + 0.05 * sin(u_AngleRadian), a_Position[1], a_Position[2], a_Position[3]);
        return;
       }else if(u_GroupComponent == 4){
        v_Position = vec3(u_Matrix * vec4(a_Position[0] + u_OffsetLocation[0],
                                            a_Position[1] + u_OffsetLocation[1],
                                            a_Position[2], a_Position[3]));
        gl_Position = u_Matrix *  vec4(a_Position[0] + u_OffsetLocation[0],
                                                                             a_Position[1] + u_OffsetLocation[1],
                                                                             a_Position[2], a_Position[3]);
        return;
        }
       v_Position = vec3(u_Matrix * a_Position);
       gl_Position = u_Matrix * a_Position;
}
