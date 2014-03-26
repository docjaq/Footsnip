#version 330

//Passed in
layout(location = 0) in vec3 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec3 normal;

out vec4 diffuseColor;
out vec3 vertexNormal;
out vec3 cameraSpacePosition;

//From CPU
uniform mat4 modelToCameraMatrix;
uniform mat3 normalModelToCameraMatrix;

//Water specifics
uniform float waterHeight;
uniform float time;
uniform int numWaves;
uniform float amplitude[8];
uniform float wavelength[8];
uniform float speed[8];
uniform vec2 direction[8];

uniform Projection
{
	mat4 cameraToClipMatrix;
};

void main()
{
    vec4 adjustedPosition = vec4(position.x, position.y, -0.45, 1);
    vec3 adjustedNormal = vec3(normal.x, normal.y, normal.z);
    
	vec4 tempCamPosition = modelToCameraMatrix * adjustedPosition;
	gl_Position = cameraToClipMatrix * tempCamPosition;
	
	vertexNormal = normalModelToCameraMatrix * adjustedNormal;
	diffuseColor = vec4(0.07686274509802, 0.46392156862734, 0.55176470588222, 0.5);
	cameraSpacePosition = vec3(tempCamPosition);
}
