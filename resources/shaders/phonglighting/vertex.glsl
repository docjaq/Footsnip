#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec3 normal;

out vec4 diffuseColor;
out vec3 vertexNormal;
out vec3 cameraSpacePosition;
out vec4 pass_Color;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

in vec4 in_Color;

uniform mat4 modelMatrix;
uniform mat3 normalMatrix;

void main()
{
	//gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_Position;
	//vertexNormal = projectionMatrix * viewMatrix * modelMatrix * normal;
	//diffuseColor = inDiffuseColor;
	//cameraSpacePosition = vec3(gl_Position.x, gl_Position.y, gl_Position.z);
	
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * position;
	vertexNormal = normalMatrix * normal;
	diffuseColor = inDiffuseColor;
	
	pass_Color = in_Color;
}

//From original
//vec4 tempCamPosition = (modelToCameraMatrix * vec4(position, 1.0));
//gl_Position = cameraToClipMatrix * tempCamPosition;

//vertexNormal = normalModelToCameraMatrix * normal;
//diffuseColor = inDiffuseColor;
//cameraSpacePosition = vec3(tempCamPosition);

