#version 400

layout(triangles, equal_spacing, ccw) in;
in vec4 tcDiffuseColor[];
in vec3 tcVertexNormal[];
in vec3 tcPosition[];

out vec4 teDiffuseColor;
out vec3 teVertexNormal;
out vec3 tePosition;
out vec3 tePatchDistance;

//From CPU
uniform mat4 modelToCameraMatrix;
uniform mat3 normalModelToCameraMatrix;
uniform Projection
{
	mat4 cameraToClipMatrix;
};
uniform sampler2D heightMap;

//float rand(vec2 co){
//    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
//}

void main()
{
    vec4 c0 = gl_TessCoord.x * tcDiffuseColor[0];
    vec4 c1 = gl_TessCoord.y * tcDiffuseColor[1];
    vec4 c2 = gl_TessCoord.z * tcDiffuseColor[2];
    teDiffuseColor = (c0 + c1 + c2);
    
    vec3 n0 = gl_TessCoord.x * tcVertexNormal[0];
    vec3 n1 = gl_TessCoord.y * tcVertexNormal[1];
    vec3 n2 = gl_TessCoord.z * tcVertexNormal[2];
    teVertexNormal = normalModelToCameraMatrix * normalize(n0 + n1 + n2);
    
    vec3 p0 = gl_TessCoord.x * tcPosition[0];
    vec3 p1 = gl_TessCoord.y * tcPosition[1];
    vec3 p2 = gl_TessCoord.z * tcPosition[2];
    tePosition = vec3(p0 + p1 + p2);
    //tePosition.z += (rand(vec2(tePosition))*0.04);
    vec2 texCoordinates = vec2(tePosition);
    texCoordinates.x +=0.5;
    texCoordinates.y +=0.5;
    tePosition.z = texture(heightMap, texCoordinates).r/3-.3;
    
    vec4 tempCamPosition = modelToCameraMatrix * vec4(tePosition, 1.0);
    tePosition = vec3(tempCamPosition);
	gl_Position = cameraToClipMatrix * tempCamPosition;
    
    tePatchDistance = gl_TessCoord;
}