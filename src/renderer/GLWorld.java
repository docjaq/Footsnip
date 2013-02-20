package renderer;

import java.nio.FloatBuffer;

import maths.LinearAlgebra;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


public class GLWorld {
	// Public for speed...
	public Matrix4f projectionMatrix;
	public Matrix4f viewMatrix;
	public Matrix4f modelMatrix;
	public FloatBuffer matrix44Buffer = null;
	private int width;
	private int height;

	public int projectionMatrixLocation;
	public int viewMatrixLocation;
	public int modelMatrixLocation;

	// I *think* this should be here.
	public Vector3f cameraPos;

	public GLWorld(int width, int height, Vector3f cameraPos) {
		this.width = width;
		this.height = height;
		this.cameraPos = cameraPos;
		setupMatrices();
	}

	private void setupMatrices() {
		// Setup projection matrix
		projectionMatrix = new Matrix4f();
		float fieldOfView = 45f;
		float aspectRatio = (float) width / (float) height;
		float near_plane = 0.1f;
		float far_plane = 100f;

		float y_scale = LinearAlgebra.coTangent(LinearAlgebra.degreesToRadians(fieldOfView / 2f));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);

		// Setup view matrix
		viewMatrix = new Matrix4f();

		// Setup model matrix
		modelMatrix = new Matrix4f();

		// Create a FloatBuffer with the proper size to store our matrices later
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
}
