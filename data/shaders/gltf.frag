#version 450

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;
layout(location = 1) in mat3 v_TBN;

layout(std140, binding = 2) uniform MaterialUBO {
	vec3 albedo;
	vec3 occRghMtl;
} u_Material;

layout(binding = 0) uniform sampler2D u_AlbedoTex;
layout(binding = 1) uniform sampler2D u_NormalTex;
layout(binding = 2) uniform sampler2D u_MetallicRoughnessTex;

void main() {
	vec3 albedo = texture(u_AlbedoTex, v_TexCoord).xyz * u_Material.albedo;
	vec3 N = normalize((texture(u_NormalTex, v_TexCoord).xyz * 2.0 - 1.0) * v_TBN);
	vec3 occRghMtl = texture(u_MetallicRoughnessTex, v_TexCoord).xyz * u_Material.occRghMtl;
	float ambientOcclussion = occRghMtl.x;
	float roughness = occRghMtl.y;
	float metallic = occRghMtl.z;

	out_Color.xyz = albedo * max(dot(N, normalize(vec3(1, -1, 1))) * 2, 0.1);
	//out_Color.xyz = vec3(metallicRoughness.x);
	out_Color.w = 1;
}