#version 450

layout(location = 0) in vec3 i_vertex;
layout(location = 1) in vec2 i_texcoord;
layout(location = 2) in vec3 i_normal;

layout(location = 0) out vec2 v_texcoord;

void main() {
	v_texcoord = i_texcoord;
	
	gl_Position = vec4(i_vertex.x, i_vertex.y, i_vertex.z, 1);
}