#version 450

layout(location = 0) in vec3 in_Position;

layout(std140, binding = 0) uniform FrameUBO {
	mat4 projection;
	mat4 view;
} u_Frame;

layout(std140, binding = 1) uniform ModelUBO {
	mat4 transform;
} u_Model;

void main() {
	gl_Position = u_Frame.projection * u_Frame.view * u_Model.transform * vec4(in_Position, 1.0);
}