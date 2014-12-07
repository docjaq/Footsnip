package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

public class GLMesh extends GLModel {

	public int numTriangles;
	public ByteBuffer indexByteBuffer;
	public int indexStride;

	public int numVertices;
	public ByteBuffer verticesByteBuffer;
	// private FloatBuffer verticesFloatBuffer;
	public int vertexStride;

	private List<GLTriangle> triangleList;
	private List<GLVertex> vertexList;

	// A state that is checked when pushing to GPU.
	private boolean buffersInstantiated;

	public GLMesh(List<GLTriangle> triangleList, List<GLVertex> vertexList) {
		super();

		this.triangleList = triangleList;
		this.vertexList = vertexList;

		buffersInstantiated = false;
	}

	public void instantiateBuffersLocally() {
		numTriangles = triangleList.size();
		indexStride = 3 * 4; // TODO: Guess

		numVertices = vertexList.size();
		vertexStride = GLVertex.stride;

		verticesByteBuffer = BufferUtils.createByteBuffer(vertexList.size() * GLVertex.stride).order(ByteOrder.nativeOrder());
		// Converted to a floatbuffer her to provide the convenience method of
		// adding an array (the whole stride). If this turns out to be slow,
		// implement another solution
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (GLVertex v : vertexList) {
			verticesFloatBuffer.put(v.getElements());
		}
		// verticesFloatBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		int[] indices = new int[triangleList.size() * 3];
		int index = 0;
		for (GLTriangle t : triangleList) {
			indices[index++] = t.v0.index;
			indices[index++] = t.v1.index;
			indices[index++] = t.v2.index;
		}

		indicesCount = indices.length;
		indexByteBuffer = BufferUtils.createByteBuffer(indicesCount * 4).order(ByteOrder.nativeOrder());
		// IntBuffer indicesBuffer = indexByteBuffer.asIntBuffer();
		for (int i = 0; i < indices.length; i++) {
			indexByteBuffer.putInt(indices[i]);
		}

		indexByteBuffer.flip();

		buffersInstantiated = true;
	}

	public void pushToGPU() {

		if (!buffersInstantiated)
			instantiateBuffersLocally();

		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesByteBuffer, GL15.GL_STATIC_DRAW);

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
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexByteBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		exitOnGLError("Creating mesh");

		setModelRadius(computeRadius(vertexList));
	}

	private float computeRadius(List<GLVertex> vertexList) {
		float maxDist = 0;
		float currentDist = 0;
		for (GLVertex v : vertexList) {
			currentDist = v.getXYZ().length();
			if (currentDist > maxDist) {
				maxDist = currentDist;
			}
		}
		float radius = maxDist;

		return radius;
	}

	public List<GLTriangle> getTriangles() {
		return triangleList;
	}

	public void setTriangles(List<GLTriangle> triangleList) {
		this.triangleList = triangleList;
	}

	public List<GLVertex> getVertices() {
		return vertexList;
	}

	public void setVertices(List<GLVertex> vertexList) {
		this.vertexList = vertexList;
	}

}
