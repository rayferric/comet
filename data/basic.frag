#version 450

layout(location = 0) in vec2 v_TexCoord;

layout(location = 0) out vec4 out_FragColor;

layout(binding = 0) uniform sampler2D u_ColorTex;

void main() {
	out_FragColor = texture(u_ColorTex, v_TexCoord);
}