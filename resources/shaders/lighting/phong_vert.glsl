#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec4 normal;

out vec4 diffuseColor;
out vec3 vertexNormal;
out vec3 cameraSpacePosition;

//Test

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

//in vec4 in_Color;

uniform mat4 modelMatrix;
uniform mat4 normalMatrix;

void main() {

	gl_Position = projectionMatrix * viewMatrix *  modelMatrix * position;
	
	vertexNormal = (projectionMatrix * viewMatrix * normalMatrix * normal).xyz;
	
	diffuseColor = inDiffuseColor;
	
    cameraSpacePosition = gl_Position.xyz;
}

