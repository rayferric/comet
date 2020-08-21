#version 450

layout(location = 0) in vec3 in_Position;
layout(location = 1) in vec2 in_TexCoord;
layout(location = 2) in vec3 in_Normal;
layout(location = 3) in vec3 in_Tangent;

layout(location = 0) out vec2 v_TexCoord;

void main() {
	v_TexCoord = in_TexCoord;
	
	gl_Position = vec4(in_Position, 1);
}