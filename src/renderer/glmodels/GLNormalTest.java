package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderer.glprimitives.GLVertex;
import renderer.glshaders.GLShader;

public class GLNormalTest extends GLModel {

	public GLVertex[] vertices = null;
	public ByteBuffer verticesByteBuffer = null;
	private Vector4f rgba;
	private List<GLVertex> vertexList;

	public GLNormalTest(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, GLShader shader, float[] color, String textureLocation) {
		super(modelPos, modelAngle, modelScale, shader, color);

		randomiseRGB();

		// TODO: Messy as fuck, but good to see it seems like it's working, for
		// debugging purposes
		vertexList = new ArrayList<GLVertex>();

		GLVertex tA_0 = new GLVertex(0f, 0f, -1f, rgba);
		GLVertex tA_1 = new GLVertex(1f, 0f, 0f, rgba);
		GLVertex tA_2 = new GLVertex(0f, 1f, 0f, rgba);
		addNormalToTriangle(tA_0, tA_1, tA_2);
		addTriangleToList(tA_0, tA_1, tA_2);

		GLVertex tB_0 = new GLVertex(0f, 0f, -1f, rgba);
		GLVertex tB_1 = new GLVertex(0f, 1f, 0f, rgba);
		GLVertex tB_2 = new GLVertex(-1f, 0f, 0f, rgba);
		addNormalToTriangle(tB_0, tB_1, tB_2);
		addTriangleToList(tB_0, tB_1, tB_2);

		GLVertex tC_0 = new GLVertex(0f, 0f, -1f, rgba);
		GLVertex tC_1 = new GLVertex(-1f, 0f, 0f, rgba);
		GLVertex tC_2 = new GLVertex(0f, -1f, 0f, rgba);
		addNormalToTriangle(tC_0, tC_1, tC_2);
		addTriangleToList(tC_0, tC_1, tC_2);

		GLVertex tD_0 = new GLVertex(0f, 0f, -1f, rgba);
		GLVertex tD_1 = new GLVertex(0f, -1f, 0f, rgba);
		GLVertex tD_2 = new GLVertex(1f, 0f, 0f, rgba);
		addNormalToTriangle(tD_0, tD_1, tD_2);
		addTriangleToList(tD_0, tD_1, tD_2);

		//

		GLVertex tE_0 = new GLVertex(0f, 0f, 1f, rgba);
		GLVertex tE_1 = new GLVertex(1f, 0f, 0f, rgba);
		GLVertex tE_2 = new GLVertex(0f, 1f, 0f, rgba);
		addNormalToTriangle(tE_0, tE_2, tE_1);
		addTriangleToList(tE_0, tE_2, tE_1);

		GLVertex tF_0 = new GLVertex(0f, 0f, 1f, rgba);
		GLVertex tF_1 = new GLVertex(0f, 1f, 0f, rgba);
		GLVertex tF_2 = new GLVertex(-1f, 0f, 0f, rgba);
		addNormalToTriangle(tF_0, tF_2, tF_1);
		addTriangleToList(tF_0, tF_2, tF_1);

		GLVertex tG_0 = new GLVertex(0f, 0f, 1f, rgba);
		GLVertex tG_1 = new GLVertex(-1f, 0f, 0f, rgba);
		GLVertex tG_2 = new GLVertex(0f, -1f, 0f, rgba);
		addNormalToTriangle(tG_0, tG_2, tG_1);
		addTriangleToList(tG_0, tG_2, tG_1);

		GLVertex tH_0 = new GLVertex(0f, 0f, 1f, rgba);
		GLVertex tH_1 = new GLVertex(0f, -1f, 0f, rgba);
		GLVertex tH_2 = new GLVertex(1f, 0f, 0f, rgba);
		addNormalToTriangle(tH_0, tH_2, tH_1);
		addTriangleToList(tH_0, tH_2, tH_1);

		GLVertex[] vertices = vertexList.toArray(new GLVertex[vertexList.size()]);

		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * GLVertex.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {

			System.out.println(vertices[i].getElements()[0] + " " + vertices[i].getElements()[1] + " " + vertices[i].getElements()[2] + " "
					+ vertices[i].getElements()[8] + " " + vertices[i].getElements()[9] + " " + vertices[i].getElements()[10]);

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
		for (int i = 2; i < indices.length; i = i + 3) {
			System.out.println("3 " + indices[i - 2] + " " + indices[i - 1] + " " + indices[i]);
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
		GL20.glVertexAttribPointer(0, GLVertex.positionElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, GLVertex.colorElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, GLVertex.normalElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.normalByteOffset);

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

	private void randomiseRGB() {
		rgba = new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
	}

	private void addTriangleToList(GLVertex v0, GLVertex v1, GLVertex v2) {
		vertexList.add(v0);
		vertexList.add(v1);
		vertexList.add(v2);
	}

	public static void addNormalToTriangle(GLVertex v0, GLVertex v1, GLVertex v2) {
		Vector3f vn0 = Vector3f.cross(Vector3f.sub(v1.getXYZ(), v0.getXYZ(), null), Vector3f.sub(v2.getXYZ(), v0.getXYZ(), null), null);
		vn0.normalise();
		v0.setNXNYNZ(vn0);

		Vector3f vn1 = Vector3f.cross(Vector3f.sub(v2.getXYZ(), v1.getXYZ(), null), Vector3f.sub(v0.getXYZ(), v1.getXYZ(), null), null);
		vn1.normalise();
		v1.setNXNYNZ(vn1);

		Vector3f vn2 = Vector3f.cross(Vector3f.sub(v0.getXYZ(), v2.getXYZ(), null), Vector3f.sub(v1.getXYZ(), v2.getXYZ(), null), null);
		vn2.normalise();
		v2.setNXNYNZ(vn2);
	}
}
