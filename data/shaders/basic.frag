#version 450

layout(location = 0) out vec4 out_Color;

layout(location = 0) in vec2 v_TexCoord;

layout(std140, binding = 2) uniform MaterialUBO {
	vec4 color;
} u_Material;

layout(binding = 0) uniform sampler2D tex_Color;

void main() {
	out_Color = texture(tex_Color, v_TexCoord) * u_Material.color;
}