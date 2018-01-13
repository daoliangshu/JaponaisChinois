#define GR_BACKGROUND 2
#define GR_BUBBLELINE 3
#define GR_BOARDGAME 0
#define GR_LETTER 5

precision mediump float;

uniform sampler2D u_TextureUnit;
uniform float u_Transparency;
uniform int u_FragmentGroup;
uniform vec3 u_Color;
varying vec2 v_TextureCoordinates;
varying vec3 v_Light;
varying vec3 v_Position;
varying vec3 v_Normal;


void main()
{
    //if(u_GroupComponent == 2 || u_GroupComponent == 1){
    //    gl_FragColor =vec4(u_Color, u_Transparency) * (texture2D(u_TextureUnit, v_TextureCoordinates) );
    //    return;
    //}


    if(u_Transparency == 10.0){
    vec4 tmpColor = vec4(1.5, 1.5,1.5, 1) * (texture2D(u_TextureUnit, v_TextureCoordinates));
    gl_FragColor = vec4(min(tmpColor, 1.0));
    return;
    }
    if(u_FragmentGroup == GR_BUBBLELINE || u_FragmentGroup == 4){
    gl_FragColor =vec4(u_Color, u_Transparency) * (texture2D(u_TextureUnit, v_TextureCoordinates));
    return;
    }
     // Will be used for attenuation.
        float distance = length(v_Light - v_Position);

        // Get a lighting direction vector from the light to the vertex.
        vec3 lightVector = normalize(v_Light - v_Position);

        // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
        // pointing in the same direction then it will get max illumination.
        float diffuse = max(dot(v_Normal, lightVector), 0.0);

        // Add attenuation.
        diffuse =  exp(1.0) /exp((1.0 + 0.2 * distance));

        // Add ambient lighting
        diffuse = diffuse + 0.5;

        // Multiply the color by the diffuse illumination level and texture value to get final output color.
        //gl_FragColor = ( vec4(vec3(1.1, 1.1, 0.9) * diffuse , 1.0)  * texture2D(u_TextureUnit, v_TextureCoordinates) );
        float mRed = 1.0;
        if(u_Transparency  < 1.0)mRed = 6.0;
        gl_FragColor = ( vec4(min(vec3(1.0*mRed * max(0.8, diffuse), 1.0 * max(0.8, diffuse), 1.0 * max(0.8, diffuse)), 1.0) , 1.0 * u_Transparency) *
                                texture2D(u_TextureUnit, v_TextureCoordinates) );
        if(u_FragmentGroup == GR_BACKGROUND){
            if(v_Position.y > -3.20 && v_Position.y < -3.0){
                gl_FragColor *= vec4(0.3, 0.3, 0.3, 1.0);
            }
        }

}