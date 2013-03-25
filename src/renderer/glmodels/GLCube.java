package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import renderer.glprimitives.GLVertexTexure;
import renderer.glshaders.GLShader;

public class GLCube extends GLModel {

	public GLVertexTexure[] vertices = null;
	public ByteBuffer verticesByteBuffer = null;

	public GLCube(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, GLShader shader, float[] color, String textureLocation) {
		super(modelPos, modelAngle, modelScale, shader, color);

		float[] blue = { 0.0f, 0.0f, 1.0f, 1 };
		float[] red = { 1.0f, 0.0f, 0.0f, 1 };
		float[] rgba = { (float) Math.random(), (float) Math.random(), (float) Math.random(), 1 };

		// Change this to using the GLVertexNormal class
		// Compute the vertex normals...

		GLVertexTexure v0 = new GLVertexTexure(-0.5f, 0.5f, 0f, blue, 0f, 0f);
		GLVertexTexure v1 = new GLVertexTexure(-0.5f, -0.5f, 0f, blue, 0f, 1f);
		GLVertexTexure v2 = new GLVertexTexure(0.5f, -0.5f, 0f, blue, 1f, 1f);
		GLVertexTexure v3 = new GLVertexTexure(0.5f, 0.5f, 0f, blue, 1f, 0f);

		GLVertexTexure v4 = new GLVertexTexure(-0.5f, 0.5f, 1f, red, 0f, 0f);
		GLVertexTexure v5 = new GLVertexTexure(-0.5f, -0.5f, 1f, red, 0f, 1f);
		GLVertexTexure v6 = new GLVertexTexure(0.5f, -0.5f, 1f, red, 0f, 1f);
		GLVertexTexure v7 = new GLVertexTexure(0.5f, 0.5f, 1f, red, 0f, 0f);

		vertices = new GLVertexTexure[] { v0, v1, v2, v3, v4, v5, v6, v7 };

		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * GLVertexTexure.stride);
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
		GL20.glVertexAttribPointer(0, GLVertexTexure.positionElementCount, GL11.GL_FLOAT, false, GLVertexTexure.stride,
				GLVertexTexure.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, GLVertexTexure.colorElementCount, GL11.GL_FLOAT, false, GLVertexTexure.stride,
				GLVertexTexure.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, GLVertexTexure.textureElementCount, GL11.GL_FLOAT, false, GLVertexTexure.stride,
				GLVertexTexure.textureByteOffset);

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
