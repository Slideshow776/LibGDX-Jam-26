#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
varying vec2 v_texCoords;
varying LOWP vec4 v_color;
uniform sampler2D u_texture;

uniform float u_contrast;

void main()
{
    vec4 color = texture2D( u_texture, v_texCoords ) * v_color;
    color.rgb = clamp((color.rgb - 0.5) * u_contrast + 0.5, 0.0, 1.0);
    gl_FragColor = color * v_color;
}