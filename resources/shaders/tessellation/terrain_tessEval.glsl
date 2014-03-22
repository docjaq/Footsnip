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

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

//Using this to simulate the 8 possible offsets. Simply pull out
//values like .xx, .xz, etc. Saves a lot of local memory
const ivec3 offset = ivec3(-1, 0, 1);

vec3 computeNormal(vec2 co){
    float tl = abs(textureOffset(heightMap, co, offset.xx).r);
    float l = abs(textureOffset(heightMap, co, offset.xy).r);
    float bl = abs(textureOffset(heightMap, co, offset.xz).r);
    float b = abs(textureOffset(heightMap, co, offset.yz).r);
    float t = abs(textureOffset(heightMap, co, offset.yx).r);
    float br = abs(textureOffset(heightMap, co, offset.zz).r);
    float r = abs(textureOffset(heightMap, co, offset.zy).r);
    float tr = abs(textureOffset(heightMap, co, offset.zx).r);
    
    float dX = tr + 2 * r + br - tl - 2 * l - bl;
    float dY = bl + 2 * b + br - tl - 2 * t - tr;
    
    vec3 normal = vec3(dX, dY, 0.1); //1.0f / 10 (1/strength) -> precompute
    
    return normalize(normal);
}

void main(){

    //Compute position from tessellation
    vec3 p0 = gl_TessCoord.x * tcPosition[0];
    vec3 p1 = gl_TessCoord.y * tcPosition[1];
    vec3 p2 = gl_TessCoord.z * tcPosition[2];
    tePosition = vec3(p0 + p1 + p2);

    //Compute texture coords
    vec2 texCoordinates = vec2(tePosition);
    texCoordinates.x +=0.5;
    texCoordinates.y +=0.5;
    
    //Adjust position z according to heigtmap
    tePosition.z = (texture(heightMap, texCoordinates).r*2-1)*0.8-0.3;

    //Compute normal and transform to camera coordinates
    teVertexNormal = normalModelToCameraMatrix * computeNormal(texCoordinates);
    
    //Compute vertex colours according to height
    //teDiffuseColor.x=1+tePosition.z;
    //teDiffuseColor.z-=tePosition.z;
    
    float noisyZ = tePosition.z+rand(texCoordinates)*0.05;
    if(noisyZ < -0.3){
        teDiffuseColor.x=1;
        teDiffuseColor.y=0.894117;
        teDiffuseColor.z=0.7686274509802;
    }else if (noisyZ >= -0.3 && noisyZ < -0.2){
        teDiffuseColor.x=0.23921568627445;
        teDiffuseColor.y=0.56862745098025;
        teDiffuseColor.z=0.26921568627445;
    }else if (noisyZ >= -0.2 && noisyZ < -0.05){
        teDiffuseColor.x=0.3686274509803;
        teDiffuseColor.y=0.1490196078431;
        teDiffuseColor.z=0.0705882352941;
    }else{
        teDiffuseColor.x=1;
        teDiffuseColor.y=0.99;
        teDiffuseColor.z=0.99;
    }
    
    /*if(tePosition.z < -0.45){
        tePosition.z = -0.45;
        teVertexNormal = vec3(rand(texCoordinates), rand(texCoordinates), 1)*normalModelToCameraMatrix;
        teDiffuseColor.x=0;
        teDiffuseColor.y=0.2;
        teDiffuseColor.z=1;
    }*/
    
    //Transform position to camera coordinates
    vec4 tempCamPosition = modelToCameraMatrix * vec4(tePosition, 1.0);
    tePosition = vec3(tempCamPosition);
	gl_Position = cameraToClipMatrix * tempCamPosition;
    
    tePatchDistance = gl_TessCoord;
}