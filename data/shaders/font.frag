#version 450

layout(early_fragment_tests) in;

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;

layout(std140, binding = 2) uniform MaterialUBO {
	vec4 color;
	float cutoff;
	float softness;
	bool showBounds;
} u_Material;

layout(binding = 0) uniform sampler2D tex_Atlas;

void main() {
	out_Color = u_Material.color;

	float halfSoftness = u_Material.softness * 0.5;
	float from = u_Material.cutoff - halfSoftness;
	float to = u_Material.cutoff + halfSoftness;
	float dist = texture(tex_Atlas, v_TexCoord).w;
	float boundsFac = u_Material.showBounds ? 0.5 : 0.0;

	out_Color.w *= max(smoothstep(from, to, dist), boundsFac);
}