#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec3 normal;

out vec4 diffuseColor;
out vec3 vertexNormal;
out vec3 cameraSpacePosition;

//uniform mat4 modelToCameraMatrix;
//uniform mat3 normalModelToCameraMatrix;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main()
{
	//vec4 tempCamPosition = (modelToCameraMatrix * vec4(position, 1.0));
	//gl_Position = cameraToClipMatrix * tempCamPosition;
	
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_Position;

	//BELOW MUST BE CHANGED FROM modelMatrix to normalMatrix?

	vertexNormal = projectionMatrix * viewMatrix * modelMatrix * normal;
	diffuseColor = inDiffuseColor;
	//cameraSpacePosition = vec3(tempCamPosition);
	cameraSpacePosition = vec3(gl_Position.x, gl_Position.y, gl_Position.z);
}
