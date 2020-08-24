#version 450

layout(location = 0) in vec3 in_Position;
layout(location = 1) in vec2 in_TexCoord;
layout(location = 2) in vec3 in_Normal;
layout(location = 3) in vec3 in_Tangent;

layout(location = 0) out vec2 v_TexCoord;
layout(location = 1) out mat3 v_TBN;

layout(std140, binding = 0) uniform FrameUBO {
	mat4 projection;
	mat4 view;
} u_Frame;

layout(std140, binding = 1) uniform ModelUBO {
	mat4 transform;
} u_Model;

void main() {
	v_TexCoord = in_TexCoord;

	vec3 bitangent = cross(in_Normal, in_Tangent);
	
	vec3 T = mat3(u_Model.transform) * in_Tangent;
   	vec3 B = mat3(u_Model.transform) * bitangent;
   	vec3 N = mat3(u_Model.transform) * in_Normal;
	   
	v_TBN = mat3(T, B, N);
	
	gl_Position = u_Frame.projection * u_Frame.view * u_Model.transform * vec4(in_Position, 1);
}