#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec4 normal;

out vec4 diffuseColor;
out vec3 vertexNormal;
out vec3 cameraSpacePosition;

uniform mat4 modelMatrix;
uniform mat4 normalMatrix;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

//I think I should be applying a clip matrix here as well to clip the geometry
//uniform vec3 cameraSpaceLightPos;

void main()
{
	gl_Position = projectionMatrix * viewMatrix *  modelMatrix * position;
	
    vertexNormal = (projectionMatrix * viewMatrix * normalMatrix * normal).xyz;
    
	diffuseColor = inDiffuseColor;

	cameraSpacePosition = (projectionMatrix * viewMatrix * modelMatrix * position).xyz;
    
    //cameraSpaceLightPos
}
