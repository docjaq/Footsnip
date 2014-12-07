#version 400

layout(vertices = 3) out;
in vec4 vDiffuseColor[];
in vec3 vVertexNormal[];
in vec3 vPosition[];

out vec4 tcDiffuseColor[];
out vec3 tcVertexNormal[];
out vec3 tcPosition[];

uniform float tessLevelInner;
uniform float tessLevelOuter;

//#define ID gl_InvocationID

void main()
{
    int id = gl_InvocationID;
    
    tcDiffuseColor[    gl_InvocationID   ] = vDiffuseColor[id];
    tcVertexNormal[    gl_InvocationID   ] = vVertexNormal[id];
    tcPosition[    gl_InvocationID   ] = vPosition[id];
    
    if (id == 0) {
        gl_TessLevelInner[0] = tessLevelInner;
        gl_TessLevelOuter[0] = tessLevelOuter;
        gl_TessLevelOuter[1] = tessLevelOuter;
        gl_TessLevelOuter[2] = tessLevelOuter;
    }
    

}