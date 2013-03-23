package renderer.glshaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import exception.RendererException;

public class GLTexturedShader extends GLShader {

	public GLTexturedShader() throws RendererException {
		super();
		// this.glWorld = glWorld;
	}

	public void setupShaderVariables() {
		// Position information will be attribute 0
		GL20.glBindAttribLocation(programID, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(programID, 1, "in_Color");
		// Texture information will be attribute 2
		GL20.glBindAttribLocation(programID, 2, "in_TextureCoord");

		// Get matrices uniform locations
		projectionMatrixLocation = GL20.glGetUniformLocation(programID, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(programID, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(programID, "modelMatrix");
		// Allows for a colour in the fragment shader
		fragColorLocation = GL20.glGetUniformLocation(programID, "fragColor");
	}

	@Override
	public void copyUniformsToShader(Matrix4f modelMatrix, float[] color) {

		modelMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();

		GL20.glUniformMatrix4(getModelMatrixLocation(), false, matrix44Buffer);
		GL20.glUniform4f(getFragColorLocation(), color[0], color[1], color[2], color[3]);
	}
}
