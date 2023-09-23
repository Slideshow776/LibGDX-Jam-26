#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
varying vec2 v_texCoords;
varying LOWP vec4 v_color;
uniform sampler2D u_texture;

uniform vec2 u_center; // Mouse position
uniform float u_time;
uniform vec3 u_shockParams;

void main()
{
	// Get pixel coordinates
	vec2 l_texCoords = v_texCoords;

	// Get distance from center
	float distance = distance(v_texCoords, u_center);

	if ( (distance <= (u_time + u_shockParams.z)) && (distance >= (u_time - u_shockParams.z)) ) {
    	float diff = (distance - u_time);
    	float powDiff = 1.0 - pow(abs(diff*u_shockParams.x), u_shockParams.y);
    	float diffTime = diff  * powDiff;
    	vec2 diffUV = normalize(v_texCoords-u_center);
    	l_texCoords = v_texCoords + (diffUV * diffTime);
	}
	
	gl_FragColor = texture2D(u_texture, l_texCoords);
}
