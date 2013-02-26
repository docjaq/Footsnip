package renderer.glshaders;

import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.loadShader;

import org.lwjgl.opengl.GL20;

import exception.RendererException;

public abstract class GLShader {

	public int programID;
	private int vertID;
	private int fragID;

	public GLShader() throws RendererException {
		// Load the vertex shader
		vertID = loadShader("resources/shaders/moving/vertex.glsl", GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		fragID = loadShader("resources/shaders/moving/fragment.glsl", GL20.GL_FRAGMENT_SHADER);

		// Create a new shader program that links both shaders
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertID);
		GL20.glAttachShader(programID, fragID);
		GL20.glLinkProgram(programID);

		setupShaderVariables();

		GL20.glValidateProgram(programID);

		exitOnGLError("setupShaders");
	}

	public abstract void setupShaderVariables();

	public void destroy() {
		GL20.glDetachShader(programID, vertID);
		GL20.glDetachShader(programID, fragID);

		GL20.glDeleteShader(vertID);
		GL20.glDeleteShader(fragID);
		GL20.glDeleteProgram(programID);
	}
}
