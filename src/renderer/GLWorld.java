package renderer;

import static maths.LinearAlgebra.degreesToRadians;

import java.nio.FloatBuffer;

import maths.LinearAlgebra;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.glshaders.GLShader;
import renderer.glshaders.GLWorldShader;
import assets.AbstractEntity;
import exception.RendererException;

public class GLWorld {

	public static final Vector3f BASIS_X = new Vector3f(1, 0, 0);
	public static final Vector3f BASIS_Y = new Vector3f(0, 1, 0);
	public static final Vector3f BASIS_Z = new Vector3f(0, 0, 1);

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

	private GLShader worldShader;

	public GLWorld(int width, int height, Vector3f cameraPos) throws RendererException {
		this.width = width;
		this.height = height;
		this.cameraPos = cameraPos;

		setupMatrices();

		worldShader = new GLWorldShader(this);
		worldShader.create();
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

	// Maybe don't need new objects here
	public void clearMatricies() {
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
	}

	public void transformCamera() {
		Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
	}

	public void transformEntity(AbstractEntity entity) {
		Matrix4f.scale(entity.getModel().modelScale, modelMatrix, modelMatrix);
		Matrix4f.translate(entity.getModel().modelPos, modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(entity.getModel().modelAngle.z), GLWorld.BASIS_Z, modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(entity.getModel().modelAngle.y), GLWorld.BASIS_Y, modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(entity.getModel().modelAngle.x), GLWorld.BASIS_X, modelMatrix, modelMatrix);
	}

	public void cleanUp() {
		projectionMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
		viewMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
		modelMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
	}

	public void startShader() {
		GL20.glUseProgram(worldShader.programID);
	}

	public void stopShader() {
		GL20.glUseProgram(0);
	}

	// Maybe clean up more stuff here?
	public void destroy() {
		// Set no shaders in use
		GL20.glUseProgram(0);

		// Clean up world shader
		worldShader.destroy();
	}
}
