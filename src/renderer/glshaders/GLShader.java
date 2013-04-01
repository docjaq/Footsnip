package renderer.glshaders;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.MessageFormat;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import exception.RendererException;

public abstract class GLShader {

	protected int programID;
	private int vertID;
	private int fragID;
	protected int fragColorLocation;
	protected int modelMatrixLocation;
	protected int projectionMatrixLocation;
	protected int viewMatrixLocation;

	protected FloatBuffer matrix44Buffer = null;

	public GLShader() {
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}

	public void create(String[] shaderName) throws RendererException {
		// Load the vertex shader
		vertID = loadShader(shaderName[0], GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		fragID = loadShader(shaderName[1], GL20.GL_FRAGMENT_SHADER);

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

	public abstract void copyUniformsToShader(Matrix4f modelMatrix, float[] color);

	public void destroy() {
		GL20.glDetachShader(programID, vertID);
		GL20.glDetachShader(programID, fragID);

		GL20.glDeleteShader(vertID);
		GL20.glDeleteShader(fragID);
		GL20.glDeleteProgram(programID);
	}

	private static int loadShader(String filename, int type) throws RendererException {
		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			String message = "Could not compile shader from file: {0}. The error was: {1}";
			throw new RendererException(MessageFormat.format(message, filename, GL20.glGetShaderInfoLog(shaderID, GL20.GL_INFO_LOG_LENGTH)));
		}

		// TODO: Should this be in a catch block further up the stack?
		exitOnGLError("loadShader");

		return shaderID;
	}

	public void bindShader() {
		GL20.glUseProgram(programID);
	}

	public void unbindShader() {
		GL20.glUseProgram(0);
	}

	public int getProgramID() {
		return programID;
	}

	public void setProgramID(int programID) {
		this.programID = programID;
	}

	public int getModelMatrixLocation() {
		return modelMatrixLocation;
	}

	public int getProjectionMatrixLocation() {
		return projectionMatrixLocation;
	}

	public int getViewMatrixLocation() {
		return viewMatrixLocation;
	}

	public int getFragColorLocation() {
		return fragColorLocation;
	}

}
