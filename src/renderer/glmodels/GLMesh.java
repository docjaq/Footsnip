package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

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

	// private List<GLVertex> vertexList;
	// private List<GLTriangle> triangleList;

	public GLMesh(List<GLTriangle> triangleList, List<GLVertex> vertexList, Vector3f modelPos, Vector3f modelAngle, float modelScale,
			GLShader shader, float[] color) {
		super(modelPos, modelAngle, modelScale, shader, color);

		// this.triangleList = triangleList;
		// this.vertexList = vertexList;
		// vertexList = mesh.getVertices();
		// mesh.normaliseAndCentre(vertexList); //Shit
		// triangleList = mesh.getTriangles();

		FloatBuffer verticesFloatBuffer = BufferUtils.createFloatBuffer(vertexList.size() * GLVertex.stride);
		for (GLVertex v : vertexList) {
			verticesFloatBuffer.put(v.getElements());
		}
		verticesFloatBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		int[] indices = new int[triangleList.size() * 3];
		int index = 0;
		for (GLTriangle t : triangleList) {
			indices[index++] = t.v0.index;
			indices[index++] = t.v1.index;
			indices[index++] = t.v2.index;
		}

		indicesCount = indices.length;
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STATIC_DRAW);

		// Loads all vertex data into a single VBO to save memory on overheads,
		// and to reduce the number of GL calls made
		GL20.glVertexAttribPointer(0, GLVertex.positionElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.positionByteOffset);
		GL20.glVertexAttribPointer(1, GLVertex.colorElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.colorByteOffset);
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

		setRadius(vertexList);
	}

	private void setRadius(List<GLVertex> vertexList) {
		float maxDist = 0;
		float currentDist = 0;
		for (GLVertex v : vertexList) {
			currentDist = v.getXYZ().length();
			if (currentDist > maxDist) {
				maxDist = currentDist;
			}
		}
		this.radius = maxDist;
		this.radius *= getModelScale();
	}

	/*
	 * I don't really think this should be here. GLMesh currently assumes it
	 * already has normals
	 */
	/*
	 * public static void addNormalToTriangle(GLVertex v0, GLVertex v1, GLVertex
	 * v2) { Vector3f vn0 = Vector3f.cross(Vector3f.sub(v1.getXYZ(),
	 * v0.getXYZ(), null), Vector3f.sub(v2.getXYZ(), v0.getXYZ(), null), null);
	 * vn0.normalise(); v0.setNXNYNZ(vn0);
	 * 
	 * Vector3f vn1 = Vector3f.cross(Vector3f.sub(v2.getXYZ(), v1.getXYZ(),
	 * null), Vector3f.sub(v0.getXYZ(), v1.getXYZ(), null), null);
	 * vn1.normalise(); v1.setNXNYNZ(vn1);
	 * 
	 * Vector3f vn2 = Vector3f.cross(Vector3f.sub(v0.getXYZ(), v2.getXYZ(),
	 * null), Vector3f.sub(v1.getXYZ(), v2.getXYZ(), null), null);
	 * vn2.normalise(); v2.setNXNYNZ(vn2); }
	 */
}
