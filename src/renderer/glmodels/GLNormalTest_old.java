package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import maths.LinearAlgebra;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import renderer.glprimitives.GLVertex_normal_old;
import renderer.glshaders.GLShader;

public class GLNormalTest_old extends GLModel {

	public GLVertex_normal_old[] vertices = null;
	public ByteBuffer verticesByteBuffer = null;
	private float[] rgba;
	private List<GLVertex_normal_old> vertexList;

	public GLNormalTest_old(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, GLShader shader, float[] color) {
		super(modelPos, modelAngle, modelScale, shader, color);

		randomiseRGB();

		// TODO: Messy as fuck, but good to see it seems like it's working, for
		// debugging purposes
		vertexList = new ArrayList<GLVertex_normal_old>();

		GLVertex_normal_old tA_0 = new GLVertex_normal_old(0f, 0f, -1f, rgba);
		GLVertex_normal_old tA_1 = new GLVertex_normal_old(1f, 0f, 0f, rgba);
		GLVertex_normal_old tA_2 = new GLVertex_normal_old(0f, 1f, 0f, rgba);
		addNormalToTriangle(tA_0, tA_1, tA_2);
		addTriangleToList(tA_0, tA_1, tA_2);

		GLVertex_normal_old tB_0 = new GLVertex_normal_old(0f, 0f, -1f, rgba);
		GLVertex_normal_old tB_1 = new GLVertex_normal_old(0f, 1f, 0f, rgba);
		GLVertex_normal_old tB_2 = new GLVertex_normal_old(-1f, 0f, 0f, rgba);
		addNormalToTriangle(tB_0, tB_1, tB_2);
		addTriangleToList(tB_0, tB_1, tB_2);

		GLVertex_normal_old tC_0 = new GLVertex_normal_old(0f, 0f, -1f, rgba);
		GLVertex_normal_old tC_1 = new GLVertex_normal_old(-1f, 0f, 0f, rgba);
		GLVertex_normal_old tC_2 = new GLVertex_normal_old(0f, -1f, 0f, rgba);
		addNormalToTriangle(tC_0, tC_1, tC_2);
		addTriangleToList(tC_0, tC_1, tC_2);

		GLVertex_normal_old tD_0 = new GLVertex_normal_old(0f, 0f, -1f, rgba);
		GLVertex_normal_old tD_1 = new GLVertex_normal_old(0f, -1f, 0f, rgba);
		GLVertex_normal_old tD_2 = new GLVertex_normal_old(1f, 0f, 0f, rgba);
		addNormalToTriangle(tD_0, tD_1, tD_2);
		addTriangleToList(tD_0, tD_1, tD_2);

		//

		GLVertex_normal_old tE_0 = new GLVertex_normal_old(0f, 0f, 1f, rgba);
		GLVertex_normal_old tE_1 = new GLVertex_normal_old(1f, 0f, 0f, rgba);
		GLVertex_normal_old tE_2 = new GLVertex_normal_old(0f, 1f, 0f, rgba);
		addNormalToTriangle(tE_0, tE_2, tE_1);
		addTriangleToList(tE_0, tE_2, tE_1);

		GLVertex_normal_old tF_0 = new GLVertex_normal_old(0f, 0f, 1f, rgba);
		GLVertex_normal_old tF_1 = new GLVertex_normal_old(0f, 1f, 0f, rgba);
		GLVertex_normal_old tF_2 = new GLVertex_normal_old(-1f, 0f, 0f, rgba);
		addNormalToTriangle(tF_0, tF_2, tF_1);
		addTriangleToList(tF_0, tF_2, tF_1);

		GLVertex_normal_old tG_0 = new GLVertex_normal_old(0f, 0f, 1f, rgba);
		GLVertex_normal_old tG_1 = new GLVertex_normal_old(-1f, 0f, 0f, rgba);
		GLVertex_normal_old tG_2 = new GLVertex_normal_old(0f, -1f, 0f, rgba);
		addNormalToTriangle(tG_0, tG_2, tG_1);
		addTriangleToList(tG_0, tG_2, tG_1);

		GLVertex_normal_old tH_0 = new GLVertex_normal_old(0f, 0f, 1f, rgba);
		GLVertex_normal_old tH_1 = new GLVertex_normal_old(0f, -1f, 0f, rgba);
		GLVertex_normal_old tH_2 = new GLVertex_normal_old(1f, 0f, 0f, rgba);
		addNormalToTriangle(tH_0, tH_2, tH_1);
		addTriangleToList(tH_0, tH_2, tH_1);

		GLVertex_normal_old[] vertices = vertexList.toArray(new GLVertex_normal_old[vertexList.size()]);

		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * GLVertex_normal_old.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = new byte[vertices.length];// = { 0, 1, 2, 3, 4, 5, 6,
													// 7, 8, 9, 10, 11 };/* 1, 1
													// */
		for (byte i = 0; i < vertices.length; i++) {
			indices[i] = i;
		}
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
		GL20.glVertexAttribPointer(0, GLVertex_normal_old.positionElementCount, GL11.GL_FLOAT, false, GLVertex_normal_old.stride,
				GLVertex_normal_old.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, GLVertex_normal_old.colorElementCount, GL11.GL_FLOAT, false, GLVertex_normal_old.stride,
				GLVertex_normal_old.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, GLVertex_normal_old.normalElementCount, GL11.GL_FLOAT, false, GLVertex_normal_old.stride,
				GLVertex_normal_old.normalByteOffset);

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

	private static void addNormalToTriangle(GLVertex_normal_old v0, GLVertex_normal_old v1, GLVertex_normal_old v2) {
		float[] va0 = v0.getXYZ();
		float[] va1 = v1.getXYZ();
		float[] va2 = v2.getXYZ();

		float[] normal = LinearAlgebra.normaliseVec(LinearAlgebra.crossProduct(LinearAlgebra.subVec3D(va1, va0),
				LinearAlgebra.subVec3D(va2, va0)));
		v0.setNXNYNZ(normal);

		float[] normal2 = LinearAlgebra.normaliseVec(LinearAlgebra.crossProduct(LinearAlgebra.subVec3D(va2, va1),
				LinearAlgebra.subVec3D(va0, va1)));
		v1.setNXNYNZ(normal2);

		float[] normal3 = LinearAlgebra.normaliseVec(LinearAlgebra.crossProduct(LinearAlgebra.subVec3D(va0, va2),
				LinearAlgebra.subVec3D(va1, va2)));
		v2.setNXNYNZ(normal3);
	}

	private void randomiseRGB() {
		rgba = new float[] { (float) Math.random(), (float) Math.random(), (float) Math.random(), 1 };
	}

	private void addTriangleToList(GLVertex_normal_old v0, GLVertex_normal_old v1, GLVertex_normal_old v2) {
		vertexList.add(v0);
		vertexList.add(v1);
		vertexList.add(v2);
	}
}