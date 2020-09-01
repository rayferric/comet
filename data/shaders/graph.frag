#version 450

layout(early_fragment_tests) in;

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;

vec3 hsvToRgb(vec3 hsv) {
    vec4 k = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(hsv.xxx + k.xyz) * 6.0 - k.www);
    return hsv.z * mix(k.xxx, clamp(p - k.xxx, 0.0, 1.0), hsv.y);
}

void main() {
	float value = (1.0 - v_TexCoord.y);
	float hue = value * 0.333;

	out_Color.xyz = hsvToRgb(vec3(hue, 1.0, 1.0));
	out_Color.w = 1.0;
}