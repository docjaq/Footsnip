package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import maths.LinearAlgebra;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import renderer.glprimitives.GLVertexNormal;
import renderer.glshaders.GLShader;

public class GLCube extends GLModel {

	public GLVertexNormal[] vertices = null;
	public ByteBuffer verticesByteBuffer = null;

	public GLCube(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, GLShader shader, float[] color, String textureLocation) {
		super(modelPos, modelAngle, modelScale, shader, color);

		float[] blue = { 0.0f, 0.0f, 1.0f, 1 };
		float[] red = { 1.0f, 0.0f, 0.0f, 1 };
		float[] rgba = { (float) Math.random(), (float) Math.random(), (float) Math.random(), 1 };

		// TODO: Normals are all broken; because it's per-vertex, not-per
		// face, it's very difficult to compute the vertices right for a cube
		// when faces share vertices. Well, you basically can't. I'll rewrite
		// this soon so that each triangle has it's own 3 vertices. Less
		// efficient, obv, but for now will allow me to actually make sure it's
		// all working...

		GLVertexNormal v0 = new GLVertexNormal(-0.5f, 0.5f, 0f, red);
		GLVertexNormal v1 = new GLVertexNormal(-0.5f, -0.5f, 0f, red);
		GLVertexNormal v2 = new GLVertexNormal(0.5f, -0.5f, 0f, red);
		GLVertexNormal v3 = new GLVertexNormal(0.5f, 0.5f, 0f, red);

		GLVertexNormal v4 = new GLVertexNormal(-0.5f, 0.5f, 1f, red);
		GLVertexNormal v5 = new GLVertexNormal(-0.5f, -0.5f, 1f, red);
		GLVertexNormal v6 = new GLVertexNormal(0.5f, -0.5f, 1f, red);
		GLVertexNormal v7 = new GLVertexNormal(0.5f, 0.5f, 1f, red);

		LinearAlgebra.addNormalToGLVertex(v0, v1, v2);
		LinearAlgebra.addNormalToGLVertex(v1, v2, v0);
		LinearAlgebra.addNormalToGLVertex(v2, v0, v1);
		LinearAlgebra.addNormalToGLVertex(v3, v2, v0);

		LinearAlgebra.addNormalToGLVertex(v4, v5, v6);
		LinearAlgebra.addNormalToGLVertex(v5, v4, v6);
		LinearAlgebra.addNormalToGLVertex(v6, v2, v7);
		LinearAlgebra.addNormalToGLVertex(v7, v6, v4);

		vertices = new GLVertexNormal[] { v0, v1, v2, v3, v4, v5, v6, v7 };

		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * GLVertexNormal.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = { 0, 1, 2, 2, 3, 0, /* */
		0, 1, 4, 4, 5, 1, /* */
		1, 2, 5, 5, 6, 2, /* */
		3, 0, 7, 7, 4, 0, /* */
		6, 2, 7, 7, 2, 3, /* */
		6, 7, 4, 4, 5, 6 };/* 1, 1 */
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);

		// This is a bit of jiggery pokery; I think this essentially could be
		// done by having a separate VBO for position, for colour and for
		// texture, but the way it's done here is to load it all into ONE vbo,
		// and then call these commands to then separate it out in the VAO

		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, GLVertexNormal.positionElementCount, GL11.GL_FLOAT, false, GLVertexNormal.stride,
				GLVertexNormal.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, GLVertexNormal.colorElementCount, GL11.GL_FLOAT, false, GLVertexNormal.stride,
				GLVertexNormal.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, GLVertexNormal.normalElementCount, GL11.GL_FLOAT, false, GLVertexNormal.stride,
				GLVertexNormal.normalByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		exitOnGLError("setupQuad");
	}
}
