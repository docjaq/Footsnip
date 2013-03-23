#version 330 core

//uniform sampler2D texture_diffuse;

in vec4 pass_Color;
//in vec2 pass_TextureCoord;

out vec4 out_Color;

uniform vec4 fragColor;

void main(void) {
	out_Color = pass_Color;
	// Override out_Color with our texture pixel
	//out_Color = texture(texture_diffuse, pass_TextureCoord);
	out_Color = vec4(fragColor.x, fragColor.y, fragColor.z, fragColor.w);
}


