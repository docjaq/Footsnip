package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;
import io.Ply;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import renderer.glshaders.GLShader;

public class GLMesh extends GLModel {

	// public GLVertex[] vertices = null;
	public ByteBuffer verticesByteBuffer = null;
	// private Vector4f rgba;
	private ArrayList<GLVertex> vertexList;
	private ArrayList<GLTriangle> triangleList;

	public GLMesh(File meshName, Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, GLShader shader, float[] color) {
		super(modelPos, modelAngle, modelScale, shader, color);

		Ply mesh = new Ply();
		mesh.read(meshName);
		vertexList = mesh.getVertices();
		// mesh.normaliseAndCentre(vertexList);// This is the shittest way ever
		// of coding this
		triangleList = mesh.getTriangles();

		for (GLTriangle t : triangleList) {
			// IMPLEMENT:
			// Assign normals if they don't exist
		}

		/**
		 * NOTE: Well, the contents of the vertexList and the triangleList
		 * appear correct, so I'm just putting them into the buffer incorrectly.
		 * Needs debugging. Or me not being drunk
		 **/

		verticesByteBuffer = BufferUtils.createByteBuffer(vertexList.size() * GLVertex.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (GLVertex v : vertexList) {

			System.out.println(v.getElements()[0] + " " + v.getElements()[1] + " " + v.getElements()[2] + " " + v.getElements()[8] + " "
					+ v.getElements()[9] + " " + v.getElements()[10]);
			verticesFloatBuffer.put(v.getElements());
		}
		verticesFloatBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = new byte[triangleList.size() * 3];
		int index = 0;
		for (GLTriangle t : triangleList) {
			System.out.println("3 " + t.v0.index + " " + t.v1.index + " " + t.v2.index);
			indices[index++] = (byte) t.v0.index;
			indices[index++] = (byte) t.v1.index;
			indices[index++] = (byte) t.v2.index;
		}

		System.out.println("PLAYER MESH DONE\n\n\n");

		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();

		/**
		 * DEBUG: Now print out the whole of both buffers here, and do the same
		 * in the GLNormalTest class. Should then be able to find the bug.
		 **/

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