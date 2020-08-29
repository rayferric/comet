#version 450

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;
layout(location = 1) in mat3 v_TBN;

layout(std140, binding = 2) uniform MaterialUBO {
	vec4 color;
	vec3 emissive;
} u_Material;

layout(binding = 0) uniform sampler2D tex_Color;
layout(binding = 1) uniform sampler2D tex_Normal;
layout(binding = 2) uniform sampler2D tex_MetallicRoughness;
layout(binding = 3) uniform sampler2D tex_Occlusion;
layout(binding = 4) uniform sampler2D tex_Emissive;

void main() {
	vec4 color = texture(tex_Color, v_TexCoord) * u_Material.color;
	vec3 albedo = color.xyz;
	float opacity = color.w;

	vec3 N = normalize((texture(tex_Normal, v_TexCoord).xyz * 2.0 - 1.0) * v_TBN);

	vec2 metallicRoughness = texture(tex_MetallicRoughness, v_TexCoord).zy;
	float metallic = metallicRoughness.x;
	float roughness = metallicRoughness.y;

	float occlusion = texture(tex_Occlusion, v_TexCoord).x;

	vec3 emissive = texture(tex_Emissive, v_TexCoord).xyz * u_Material.emissive;

	out_Color.xyz = albedo * max(dot(N, normalize(vec3(1, -1, 1))), 0.1) * occlusion + emissive;
	out_Color.w = opacity;
}