#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
varying vec2 v_texCoords;
varying LOWP vec4 v_color;
uniform sampler2D u_texture;

uniform float u_percent;
uniform vec4 u_color;

void main()
{
    vec4 color = texture2D( u_texture, v_texCoords ) * v_color;
    vec3 newColor = u_color.rgb * u_percent;
	color.rgb = color.rgb * (1.0 - u_percent) + newColor;
    gl_FragColor = color;
}