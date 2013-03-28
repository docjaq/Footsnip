#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec4 normal;

out vec4 diffuseColor;
out vec3 vertexNormal;
out vec3 cameraSpacePosition;
out vec4 pass_Color;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

//in vec4 in_Color;

uniform mat4 modelMatrix;
uniform mat4 normalMatrix;

void main()
{
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * position;
	
	vec4 tempNormal = projectionMatrix * viewMatrix * normalMatrix * normal;
	
	//TODO: Flipping the normal here to compensate for incorrectly computed normal earlier
	vertexNormal = vec3(-tempNormal.x, -tempNormal.y, -tempNormal.z);
	
	diffuseColor = inDiffuseColor;
	
	cameraSpacePosition = vec3(gl_Position.x, gl_Position.y, gl_Position.z);
}

