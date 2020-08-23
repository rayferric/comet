#version 450

layout(location = 0) in vec2 v_TexCoord;

layout(location = 0) out vec4 out_Color;

layout(std140, binding = 2) uniform MaterialUBO {
	vec3 color;
	vec3 color2;
} u_Material;

layout(binding = 0) uniform sampler2D u_ColorTex;

void main() {
	if(v_TexCoord.x < 0.5)
		out_Color.xyz = texture(u_ColorTex, v_TexCoord).xyz * u_Material.color;
	else
		out_Color.xyz = texture(u_ColorTex, v_TexCoord).xyz * u_Material.color2;
	out_Color.w = 1;
}