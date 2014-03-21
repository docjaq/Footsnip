package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;
import math.types.MatrixStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import renderer.GLPosition;
import renderer.GLWorld;
import renderer.glshaders.GLGaussianTessellationShader;
import renderer.glshaders.GLShader;

/*
 * TODO: Need to set the model centres better, as currently they're on the
 * plane I think. Not too important for now, as our cubes centres are now 0,0,0,
 * but need to make sure they're initialised to the centre of the object in future.
 */

public abstract class GLModel {

	// Model positions
	// public Vector3 modelPos;
	// public Vector3 modelAngle;
	// private Vector3 modelScale;

	// Model GL Variables
	protected int vaoId = 0;
	protected int vboId = 0;
	protected int vboiId = 0;
	protected int indicesCount = 0;

	protected float modelRadius;

	// Collision variables
	/* MAKE SURE THAT THIS RADIUS HAS BEEN SET BY THE IMPLEMENTING CLASS */
	// private float radius;

	public GLModel() {
		// Set the default quad rotation, scale and position values
		// this.modelPos = modelPos;
		// this.modelAngle = modelAngle;
		// setModelScale(modelScale);
	}

	protected void setModelRadius(float modelRadius) {
		this.modelRadius = modelRadius;
	}

	public float getModelRadius() {
		return modelRadius;
	}

	public void draw(GLShader shader, MatrixStack modelMatrix, GLPosition position) {

		// transform();
		modelMatrix.getTop().translate(position.modelPos.x(), position.modelPos.y(), position.modelPos.z());

		modelMatrix.getTop().rotateDeg(position.modelAngle.z(), GLWorld.BASIS_Z);
		modelMatrix.getTop().rotateDeg(position.modelAngle.y(), GLWorld.BASIS_Y);
		modelMatrix.getTop().rotateDeg(position.modelAngle.x(), GLWorld.BASIS_X);

		modelMatrix.getTop().scale(position.modelScale);

		shader.copySpecificUniformsToShader(modelMatrix);

		shader.copyTesselationUniformsToShader();

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

		// Debug mode
		// GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		if (shader instanceof GLGaussianTessellationShader) {
			((GLGaussianTessellationShader) shader).bindHeightmap();
			// ((GLGaussianTessellationShader) shader).bindNormalmap();
		}

		if (shader instanceof GLGaussianTessellationShader) {
			GL40.glPatchParameteri(GL40.GL_PATCH_VERTICES, 3);
			GL11.glDrawElements(GL40.GL_PATCHES, indicesCount, GL11.GL_UNSIGNED_INT, 0);
		} else {
			GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_INT, 0);
		}

		if (shader instanceof GLGaussianTessellationShader) {
			((GLGaussianTessellationShader) shader).unbindHeightmap();
			// ((GLGaussianTessellationShader) shader).unbindNormalmap();
		}

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

}
