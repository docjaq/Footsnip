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

import renderer.glprimitives.GLVertex_texCoords_old;
import renderer.glshaders.GLShader;

public class GLTexturedCube extends GLTexturedModel {

	public GLVertex_texCoords_old[] vertices = null;
	public ByteBuffer verticesByteBuffer = null;

	public GLTexturedCube(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, GLShader shader, float[] color,
			String textureLocation) {
		super(modelPos, modelAngle, modelScale, shader, color);

		float[] rgba = { 1, 0, 0, 1 };

		GLVertex_texCoords_old v0 = new GLVertex_texCoords_old(-0.5f, 0.5f, 0f, rgba, 0f, 0f);
		GLVertex_texCoords_old v1 = new GLVertex_texCoords_old(-0.5f, -0.5f, 0f, rgba, 0f, 1f);
		GLVertex_texCoords_old v2 = new GLVertex_texCoords_old(0.5f, -0.5f, 0f, rgba, 1f, 1f);
		GLVertex_texCoords_old v3 = new GLVertex_texCoords_old(0.5f, 0.5f, 0f, rgba, 1f, 0f);

		GLVertex_texCoords_old v4 = new GLVertex_texCoords_old(-0.5f, 0.5f, 1f, rgba, 0f, 0f);
		GLVertex_texCoords_old v5 = new GLVertex_texCoords_old(-0.5f, -0.5f, 1f, rgba, 0f, 1f);
		GLVertex_texCoords_old v6 = new GLVertex_texCoords_old(0.5f, -0.5f, 1f, rgba, 0f, 1f);
		GLVertex_texCoords_old v7 = new GLVertex_texCoords_old(0.5f, 0.5f, 1f, rgba, 0f, 0f);

		vertices = new GLVertex_texCoords_old[] { v0, v1, v2, v3, v4, v5, v6, v7 };

		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * GLVertex_texCoords_old.stride);
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
		6, 7, 4, 4, 5, 6 };
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
		GL20.glVertexAttribPointer(0, GLVertex_texCoords_old.positionElementCount, GL11.GL_FLOAT, false, GLVertex_texCoords_old.stride, GLVertex_texCoords_old.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, GLVertex_texCoords_old.colorElementCount, GL11.GL_FLOAT, false, GLVertex_texCoords_old.stride, GLVertex_texCoords_old.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, GLVertex_texCoords_old.textureElementCount, GL11.GL_FLOAT, false, GLVertex_texCoords_old.stride, GLVertex_texCoords_old.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		// TODO: Weird that this is being called from the child class. ALSO,
		// need to store shared textures, as this being loaded EVERY time for
		// each quad
		setupTextures(textureLocation);

		exitOnGLError("setupQuad");
	}
}
