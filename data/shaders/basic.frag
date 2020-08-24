#version 450

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;

layout(std140, binding = 2) uniform MaterialUBO {
	vec3 color;
} u_Material;

layout(binding = 0) uniform sampler2D u_ColorTex;

void main() {
	out_Color.xyz = texture(u_ColorTex, v_TexCoord).xyz * u_Material.color;
	out_Color.w = 1;
}