#version 330

//Passed in
layout(location = 0) in vec3 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec3 normal;

out vec4 diffuseColor;
out vec3 vertexNormal;
out vec3 cameraSpacePosition;
out vec2 uvPosition;

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
const float pi = 3.14159;

uniform Projection
{
	mat4 cameraToClipMatrix;
};

float wave(int i, float x, float y) {
    float frequency = 2*pi/wavelength[i];
    float phase = speed[i] * frequency;
    float theta = dot(direction[i], vec2(x, y));
    return amplitude[i] * sin(theta * frequency + time * phase);
}

float waveHeight(float x, float y) {
    float height = 0.0;
    for (int i = 0; i < numWaves; ++i)
        height += wave(i, x, y);
    return height;
}

float dWavedx(int i, float x, float y) {
    float frequency = 2*pi/wavelength[i];
    float phase = speed[i] * frequency;
    float theta = dot(direction[i], vec2(x, y));
    float A = amplitude[i] * direction[i].x * frequency;
    return A * cos(theta * frequency + time * phase);
}

float dWavedy(int i, float x, float y) {
    float frequency = 2*pi/wavelength[i];
    float phase = speed[i] * frequency;
    float theta = dot(direction[i], vec2(x, y));
    float A = amplitude[i] * direction[i].y * frequency;
    return A * cos(theta * frequency + time * phase);
}

vec3 waveNormal(float x, float y) {
    float dx = 0.0;
    float dy = 0.0;
    for (int i = 0; i < numWaves; ++i) {
        dx += dWavedx(i, x, y);
        dy += dWavedy(i, x, y);
    }
    vec3 n = vec3(-dx, -dy, 1.0);
    return normalize(n);
}

void main(){
    
    //Compute texture coords
    uvPosition = vec2(position);
    uvPosition.x +=0.5;
    uvPosition.y +=0.5;
    
    vec4 adjustedPosition = vec4(position.x, position.y, position.z, 1);
    vec3 adjustedNormal = vec3(normal.xyz);
    
    //Wave stuff
    adjustedPosition.z = waterHeight + waveHeight(adjustedPosition.x, adjustedPosition.y);
    adjustedNormal = waveNormal(adjustedPosition.x, adjustedPosition.y);
    float someVariable = waterHeight;
    
	vec4 tempCamPosition = modelToCameraMatrix * adjustedPosition;
	gl_Position = cameraToClipMatrix * tempCamPosition;
	
	vertexNormal = normalize(normalModelToCameraMatrix * adjustedNormal);
	//diffuseColor = vec4(0.07686274509802, 0.46392156862734, 0.55176470588222, 0.35);
    diffuseColor = vec4(0.7, 0.7, 0.8, 1);
	cameraSpacePosition = vec3(tempCamPosition);
    
    
    
}
