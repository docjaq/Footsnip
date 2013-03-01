package renderer.glshaders;

import org.lwjgl.opengl.GL20;

import renderer.GLWorld;
import exception.RendererException;

public class GLWorldShader extends GLShader {

	private GLWorld glWorld;

	public GLWorldShader(GLWorld glWorld) throws RendererException {
		// super();
		this.glWorld = glWorld;
	}

	public void setupShaderVariables() {
		// Position information will be attribute 0
		GL20.glBindAttribLocation(programID, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(programID, 1, "in_Color");
		// Texture information will be attribute 2
		GL20.glBindAttribLocation(programID, 2, "in_TextureCoord");

		// Get matrices uniform locations
		glWorld.projectionMatrixLocation = GL20.glGetUniformLocation(programID, "projectionMatrix");
		glWorld.viewMatrixLocation = GL20.glGetUniformLocation(programID, "viewMatrix");
		glWorld.modelMatrixLocation = GL20.glGetUniformLocation(programID, "modelMatrix");
		// Allows for a colour in the fragment shader
		glWorld.fragColorLocation = GL20.glGetUniformLocation(programID, "fragColor");
	}
}
