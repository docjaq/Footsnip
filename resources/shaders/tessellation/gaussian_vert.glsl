#version 400

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 inDiffuseColor;
layout(location = 2) in vec3 normal;

out vec4 vDiffuseColor;
out vec3 vVertexNormal;
out vec3 vPosition;

void main()
{
    vDiffuseColor = inDiffuseColor;
	vVertexNormal = normal;
    //Hack to remove seams for now
	vPosition = position*1.004-0.002;
}
