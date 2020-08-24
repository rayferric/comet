#version 450

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;
layout(location = 1) in mat3 v_TBN;

layout(std140, binding = 2) uniform MaterialUBO {
	vec3 color;
	vec3 color2;
} u_Material;

layout(binding = 0) uniform sampler2D u_ColorTex;

void main() {
	vec3 N = normalize((texture2D(normals, v_TexCoord).xyz * 2.0 - 1.0) * v_TBN);

	if(v_TexCoord.x < 0.5)
		out_Color.xyz = texture(u_ColorTex, v_TexCoord).xyz * u_Material.color;
	else
		out_Color.xyz = texture(u_ColorTex, v_TexCoord).xyz * u_Material.color2;
	out_Color.w = 1;
}