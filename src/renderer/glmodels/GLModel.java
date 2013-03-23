package renderer.glmodels;

import static maths.LinearAlgebra.degreesToRadians;
import static renderer.GLUtilityMethods.exitOnGLError;
import geometry.BoundingBox;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.GLWorld;
import renderer.glshaders.GLShader;

public class GLModel {

	// Quad Position variables
	public Vector3f modelPos;
	public Vector3f modelAngle;
	public Vector3f modelScale;

	// Quad variables
	protected int vaoId = 0;
	protected int vboId = 0;
	protected int vboiId = 0;
	protected int indicesCount = 0;

	private float[] color;

	// Model Matrix
	private Matrix4f modelMatrix;

	private GLShader shader;

	private FloatBuffer matrix44Buffer = null;

	public GLShader getShader() {
		return shader;
	}

	public void setShader(GLShader shader) {
		this.shader = shader;
	}

	public GLModel(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, GLShader shader, float[] color) {
		// Set the default quad rotation, scale and position values
		this.modelPos = modelPos;
		this.modelAngle = modelAngle;
		this.modelScale = modelScale;
		this.shader = shader;
		this.color = color;

		modelMatrix = new Matrix4f();

		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}

	public void draw() {

		transform();
		getShader().bindShader();
		copyModelMatrixToShader();

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vaoId);

		// Currently, our GLModel, therefore, only supports 3 arrays; xyz, rgb,
		// and st. If we load them in the current manner, they MUST all be
		// present
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Bind to the index VBO that has all the information about the order of
		// the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

		// Draw the vertices
		// Currently, our GLModel, therefore, can only consist of triangles
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);

		getShader().unbindShader();
	}

	public void cleanUp() {
		cleanUpGeometry();
	}

	// TODO: Check if there is a texture, as with draw()
	protected void cleanUpGeometry() {
		// Clean up geometry

		// Select the VAO
		GL30.glBindVertexArray(vaoId);

		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		// This last one was missing in the original code, but seems important
		// to clear the data for the st texture coords
		GL20.glDisableVertexAttribArray(2);

		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);
		// modelMatrix = new Matrix4f();

		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);

		exitOnGLError("destroyOpenGL");
	}

	// TODO: Implement this method
	public BoundingBox getBoundingBox() {
		return new BoundingBox();
	}

	public void transform() {
		// This order is important for building the matrix
		clearModelMatrix();
		Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
		Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.z), GLWorld.BASIS_Z, modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.y), GLWorld.BASIS_Y, modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.x), GLWorld.BASIS_X, modelMatrix, modelMatrix);
	}

	// ATTENTION: Before, had a local Floatbuffer in the class, but as it's just
	// used to copy stuff to the shader, seems a waste. Maybe a better way than
	// sending the reference around though?
	public void copyModelMatrixToShader() {
		modelMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(shader.getModelMatrixLocation(), false, matrix44Buffer);
		// This copies the colour to the (fragment) shader
		GL20.glUniform4f(shader.getFragColorLocation(), color[0], color[1], color[2], color[3]);
	}

	// public void matrixCleanup() {
	// modelMatrix.store(matrix44Buffer);
	// matrix44Buffer.flip();
	// GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
	// }

	// ATTENTION:Setting it to the identity seems more efficient than creating a
	// new one?
	protected void clearModelMatrix() {
		// modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
	}
}
