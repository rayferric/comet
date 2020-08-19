#version 460

layout(location = 0) in vec2 v_texcoord;

layout(location = 0) out vec4 o_fragcolor;

layout(binding = 0) uniform sampler2D u_texture0;
layout(binding = 1) uniform sampler2D u_texture1;

layout(location = 0) uniform float u_float;

void main() {
	vec3 tex0 = texture(u_texture0, v_texcoord).xyz;
	vec3 tex1 = texture(u_texture1, v_texcoord).xyz;
	o_fragcolor = vec4(mix(tex0, tex1, u_float) * vec3(v_texcoord.xy, 1.0), 1.0);
}