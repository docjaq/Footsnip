package renderer.glshaders;

import org.lwjgl.opengl.GL20;

import exception.RendererException;

public class GLGeneralShader extends GLShader {

	public GLGeneralShader() throws RendererException {
		super();
		// this.glWorld = glWorld;
	}

	public void setupShaderVariables() {
		// Position information will be attribute 0
		GL20.glBindAttribLocation(programID, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(programID, 1, "in_Color");
		// Texture information will be attribute 2
		// GL20.glBindAttribLocation(programID, 2, "in_TextureCoord");

		// Get matrices uniform locations
		projectionMatrixLocation = GL20.glGetUniformLocation(programID, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(programID, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(programID, "modelMatrix");
		// Allows for a colour in the fragment shader
		fragColorLocation = GL20.glGetUniformLocation(programID, "fragColor");
	}
}
