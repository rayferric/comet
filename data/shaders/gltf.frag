#version 450

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;
layout(location = 1) in mat3 v_TBN;

layout(std140, binding = 2) uniform MaterialUBO {
	vec3 albedo;
	vec3 emissive;
} u_Material;

layout(binding = 0) uniform sampler2D u_AlbedoTex;
layout(binding = 1) uniform sampler2D u_NormalTex;
layout(binding = 2) uniform sampler2D u_MetallicRoughnessTex;
layout(binding = 3) uniform sampler2D u_OcclusionTex;
layout(binding = 4) uniform sampler2D u_EmissiveTex;

void main() {
	vec3 albedo = texture(u_AlbedoTex, v_TexCoord).xyz * u_Material.albedo;

	vec3 N = normalize((texture(u_NormalTex, v_TexCoord).xyz * 2.0 - 1.0) * v_TBN);

	vec2 metallicRoughness = texture(u_MetallicRoughnessTex, v_TexCoord).zy;
	float metallic = metallicRoughness.x;
	float roughness = metallicRoughness.y;

	float occlusion = texture(u_OcclusionTex, v_TexCoord).x;

	vec3 emissive = texture(u_EmissiveTex, v_TexCoord).xyz * u_Material.emissive;

	out_Color.xyz = albedo * max(dot(N, normalize(vec3(1, -1, 1))), 0.1) * occlusion + emissive;
	out_Color.w = 1;
}