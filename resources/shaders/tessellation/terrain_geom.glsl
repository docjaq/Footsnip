#version 400

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

in vec4 teDiffuseColor[3];
in vec3 teVertexNormal[3];
in vec3 tePosition[3];
in vec3 tePatchDistance[3];

in vec2 teUvPosition[3];

out vec4 gDiffuseColor;
out vec3 gVertexNormal;
out vec3 gPosition;
out vec3 gPatchDistance;

out vec2 gUvPosition;

void main()
{
    gPatchDistance = tePatchDistance[0];
    gDiffuseColor = teDiffuseColor[0];
    gVertexNormal = teVertexNormal[0];
    gPosition = tePosition[0];
    gUvPosition = teUvPosition[0];
    gl_Position = gl_in[0].gl_Position; EmitVertex();
    
    gPatchDistance = tePatchDistance[1];
    gDiffuseColor = teDiffuseColor[1];
    gVertexNormal = teVertexNormal[1];
    gPosition = tePosition[1];
    gUvPosition = teUvPosition[1];
    gl_Position = gl_in[1].gl_Position; EmitVertex();
    
    gPatchDistance = tePatchDistance[2];
    gDiffuseColor = teDiffuseColor[2];
    gVertexNormal = teVertexNormal[2];
    gPosition = tePosition[2];
    gUvPosition = teUvPosition[2];
    gl_Position = gl_in[2].gl_Position; EmitVertex();
    
    EndPrimitive();
}