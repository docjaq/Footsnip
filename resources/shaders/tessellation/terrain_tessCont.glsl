#version 400

layout(vertices = 3) out;
in vec4 vDiffuseColor[];
in vec3 vVertexNormal[];
in vec3 vPosition[];

out vec4 tcDiffuseColor[];
out vec3 tcVertexNormal[];
out vec3 tcPosition[];

uniform float TessLevelInner;
uniform float TessLevelOuter;

//#define ID gl_InvocationID

void main()
{
    int id = gl_InvocationID;
    
    tcDiffuseColor[    gl_InvocationID   ] = vDiffuseColor[id];
    tcVertexNormal[    gl_InvocationID   ] = vVertexNormal[id];
    tcPosition[    gl_InvocationID   ] = vPosition[id];
    
    if (id == 0) {
        gl_TessLevelInner[0] = TessLevelInner;
        gl_TessLevelOuter[0] = TessLevelOuter;
        gl_TessLevelOuter[1] = TessLevelOuter;
        gl_TessLevelOuter[2] = TessLevelOuter;
    }
    

}