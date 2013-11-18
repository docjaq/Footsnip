package renderer;

import java.nio.FloatBuffer;

import maths.LinearAlgebra;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.glshaders.GLShader;
import exception.RendererException;

public class GLWorld {

	public static final Vector3f BASIS_X = new Vector3f(1, 0, 0);
	public static final Vector3f BASIS_Y = new Vector3f(0, 1, 0);
	public static final Vector3f BASIS_Z = new Vector3f(0, 0, 1);

	public Matrix4f projectionMatrix;
	public Matrix4f viewMatrix;
	// * public Matrix4f modelMatrix;
	private FloatBuffer matrix44Buffer = null;
	private int width;
	private int height;

	private Vector3f cameraPos;

	public void setCameraPos(Vector3f cameraPos) {
		this.cameraPos.x = -cameraPos.x;
		this.cameraPos.y = -cameraPos.y;
		this.cameraPos.z = -cameraPos.z;
	}

	public GLWorld(int width, int height, Vector3f cameraPos) throws RendererException {
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
		// * modelMatrix = new Matrix4f();

		// Create a FloatBuffer with the proper size to store our matrices later
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}

	public void clearViewMatrix() {
		viewMatrix.setIdentity();
	}

	public void transformCamera() {
		Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
	}

	public void copyCameraMatricesToShader(GLShader shader) {
		projectionMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(shader.getProjectionMatrixLocation(), false, matrix44Buffer);
		viewMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(shader.getViewMatrixLocation(), false, matrix44Buffer);
	}
}
