#version 450

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;
layout(location = 1) in mat3 v_TBN;

layout(std140, binding = 2) uniform MaterialUBO {
	bool hasNormalMap;
	vec4 color;
	ivec2 frames;
	int frame;
} u_Material;

layout(binding = 0) uniform sampler2D tex_Color;
layout(binding = 1) uniform sampler2D tex_Normal;

void main() {
	vec2 frameSize = 1.0 / u_Material.frames;
	vec2 texCoord = v_TexCoord * frameSize;
	texCoord.x += u_Material.frame % u_Material.frames.x * frameSize.x;
	texCoord.y += u_Material.frame / u_Material.frames.x * frameSize.y;

	vec4 color = texture(tex_Color, texCoord) * u_Material.color;
	vec3 albedo = color.xyz;
	float opacity = color.w;

	vec3 normalMap = vec3(0.5, 0.5, 1);
	if(u_Material.hasNormalMap) normalMap = texture(tex_Normal, v_TexCoord).xyz;
	vec3 N = normalize((normalMap * 2.0 - 1.0) * v_TBN);

	out_Color.xyz = albedo;
	out_Color.w = opacity;
}