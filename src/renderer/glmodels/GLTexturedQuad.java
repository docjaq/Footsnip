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

import renderer.glprimitives.GLVertex;

public class GLTexturedQuad extends GLModel {

	public GLVertex[] vertices = null;
	public ByteBuffer verticesByteBuffer = null;

	public GLTexturedQuad(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale) {
		super(modelPos, modelAngle, modelScale);
		// We'll define our quad using 4 vertices of the custom 'TexturedVertex'
		// class
		GLVertex v0 = new GLVertex();
		v0.setXYZ(-0.5f, 0.5f, 0);
		v0.setRGB(1, 0, 0);
		v0.setST(0, 0);
		GLVertex v1 = new GLVertex();
		v1.setXYZ(-0.5f, -0.5f, 0);
		v1.setRGB(0, 1, 0);
		v1.setST(0, 1);
		GLVertex v2 = new GLVertex();
		v2.setXYZ(0.5f, -0.5f, 0);
		v2.setRGB(0, 0, 1);
		v2.setST(1, 1);
		GLVertex v3 = new GLVertex();
		v3.setXYZ(0.5f, 0.5f, 0);
		v3.setRGB(1, 1, 1);
		v3.setST(1, 0);

		vertices = new GLVertex[] { v0, v1, v2, v3 };

		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * GLVertex.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = { 0, 1, 2, 2, 3, 0 };
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
		GL20.glVertexAttribPointer(0, GLVertex.positionElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, GLVertex.colorElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, GLVertex.textureElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		// TODO: Not sure if this should be here on in parent class
		setupTextures();

		exitOnGLError("setupQuad");
	}

}
