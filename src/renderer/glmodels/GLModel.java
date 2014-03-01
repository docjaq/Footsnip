package renderer.glmodels;

import static maths.LinearAlgebra.degreesToRadians;
import static renderer.GLUtilityMethods.exitOnGLError;
import maths.types.MatrixStack;
import maths.types.Vector3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderer.GLWorld;
import renderer.glshaders.GLShader;

/*
 * TODO: Need to set the model centres better, as currently they're on the
 * plane I think. Not too important for now, as our cubes centres are now 0,0,0,
 * but need to make sure they're initialised to the centre of the object in future.
 */

public abstract class GLModel {

	// Model positions
	public Vector3 modelPos;
	public Vector3 modelAngle;
	private Vector3 modelScale;

	// Model GL Variables
	protected int vaoId = 0;
	protected int vboId = 0;
	protected int vboiId = 0;
	protected int indicesCount = 0;

	// Collision variables
	/* MAKE SURE THAT THIS RADIUS HAS BEEN SET BY THE IMPLEMENTING CLASS */
	private float radius;

	// protected float[] color;

	// This could actually just be static or a single object in the renderer.
	// Having one per model is a waste, though not really much of an overhead
	// for now
	// protected Matrix4f modelMatrix;

	// public String debugType;

	public GLModel(Vector3 modelPos, Vector3 modelAngle, float modelScale) {
		// Set the default quad rotation, scale and position values
		this.modelPos = modelPos;
		this.modelAngle = modelAngle;
		this.modelScale = new Vector3(1.0f, 1.0f, 1.0f);
		setModelScale(modelScale);
		// this.shader = shader;

		// modelMatrix = new Matrix4f();
	}

	public void draw(GLShader shader, MatrixStack modelMatrix) {

		// transform();
		modelMatrix.getTop().translate(modelPos.x(), modelPos.y(), modelPos.z());

		modelMatrix.getTop().rotate(degreesToRadians(modelAngle.z()), GLWorld.BASIS_Z);
		modelMatrix.getTop().rotate(degreesToRadians(modelAngle.y()), GLWorld.BASIS_Y);
		modelMatrix.getTop().rotate(degreesToRadians(modelAngle.x()), GLWorld.BASIS_X);

		shader.copySpecificUniformsToShader(modelMatrix);

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vaoId);

		// Currently, our GLModel, therefore, only supports 3 arrays; xyz, rgb,
		// and nxnynz. If we load them in the current manner, they MUST all be
		// present
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Bind to the index VBO that has all the information about the order of
		// the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

		// Draw the vertices
		// Currently, our GLModel, therefore, can only consist of triangles
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_INT, 0);
		// GL11.glDrawElements(GL11.GL_LINE_LOOP, indicesCount,
		// GL11.GL_UNSIGNED_INT, 0);

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void cleanUp() {
		cleanUpGeometry();
	}

	protected void cleanUpGeometry() {
		// Clean up geometry

		// Select the VAO
		GL30.glBindVertexArray(vaoId);

		// Disable the VBO index from the VAO attributes list, xyz, nxnynz, rgb
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);

		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);

		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);

		exitOnGLError("destroyOpenGL");
	}

	public void transform() {
		// This order is important for building the matrix
		// clearModelMatrix();

		// modelMatrix.scale(modelScale);
		// modelMatrix.translate(modelPos);

		// modelMatrix.rotate(degreesToRadians(modelAngle.z), GLWorld.BASIS_Z);
		// modelMatrix.rotate(degreesToRadians(modelAngle.y), GLWorld.BASIS_Y);
		// modelMatrix.rotate(degreesToRadians(modelAngle.x), GLWorld.BASIS_X);

	}

	// ATTENTION: Before, had a local Floatbuffer in the class, but as it's just
	// used to copy stuff to the shader, seems a waste. Maybe a better way than
	// sending the reference around though?
	// public void copyModelMatrixToShader() {

	// }

	// public void matrixCleanup() {
	// modelMatrix.store(matrix44Buffer);
	// matrix44Buffer.flip();
	// GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
	// }

	// ATTENTION:Setting it to the identity seems more efficient than creating a
	// new one?
	// protected void clearModelMatrix() {
	// modelMatrix = new Matrix4f();
	// modelMatrix.setIdentity();
	// }

	public void setModelScale(float modelScale) {
		/**
		 * This has been changed to only allow uniform scaling of the model.
		 * This means that we can compute the bounding sphere more easily at
		 * runtime
		 **/
		this.modelScale.set(modelScale, modelScale, modelScale);

		radius *= getModelScale();
	}

	public float getModelScale() {
		return modelScale.x();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
}
