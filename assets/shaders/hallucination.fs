#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

uniform sampler2D u_texture;
varying vec2 v_texCoord;
varying LOWP vec4 v_color;
uniform float u_time;

// Parameters
const float zoomFactor = 1.0; // Zoom factor (adjust as needed)
const float speedX = 0.1;    // Horizontal motion speed (adjust as needed)
const float speedY = 0.15;   // Vertical motion speed (adjust as needed)
const float maxTransparency = 0.5; // Maximum Transparency (adjust as needed)

void main() {
    // Add a random offset to u_time
    float randomOffset = mod(u_time * 0.1, 1.0); // Adjust the 0.1 factor for randomness

    // Calculate transparency as a sine wave that fades in and out
    float transparency = (sin(u_time) + 1.0) * 0.5 * maxTransparency;

    // Calculate the zoomed texture coordinates
    vec2 zoomedTexCoord = v_texCoord * zoomFactor;

    // Calculate the sinusoidal motion for both X and Y directions
    float offsetX = sin((u_time + randomOffset) * speedX) * 0.2; // Adjust the 0.2 factor for motion amplitude
    float offsetY = sin((u_time + randomOffset) * speedY) * 0.2; // Adjust the 0.2 factor for motion amplitude

    // Offset the zoomed texture coordinates with the sinusoidal motion
    zoomedTexCoord.x += offsetX;
    zoomedTexCoord.y += offsetY;

    // Ensure the offset coordinates wrap around the texture
    zoomedTexCoord = fract(zoomedTexCoord);

    // Sample the texture and apply transparency
    vec4 new_color = texture2D(u_texture, zoomedTexCoord);
    new_color.a *= transparency;
    // new_color.r *= transparency;
    // new_color.g *= transparency;
    // new_color.b *= transparency;

    vec4 old_color = texture2D(u_texture, v_texCoord) * v_color;

    gl_FragColor = old_color;
}