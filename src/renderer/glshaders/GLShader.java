package renderer.glshaders;

import static renderer.GLUtilityMethods.exitOnGLError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

import math.types.MatrixStack;
import math.types.Vector4;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import renderer.MaterialParams;
import exception.RendererException;

public abstract class GLShader {

	// Program IDs
	protected int programID;
	private int vertID;
	private int fragID;

	private int geomID;
	private int tessContID;
	private int tessEvalID;

	protected int projectionBlockIndex;

	private boolean tessellation;

	public GLShader(int projectionBlockIndex) {
		this.projectionBlockIndex = projectionBlockIndex;
		tessellation = false;
	}

	public void create(String[] shaderName) throws RendererException {

		programID = GL20.glCreateProgram();

		if (shaderName.length == 5) {
			tessellation = true;

			vertID = loadShader(shaderName[0], GL20.GL_VERTEX_SHADER);
			tessContID = loadShader(shaderName[1], GL40.GL_TESS_CONTROL_SHADER);
			tessEvalID = loadShader(shaderName[2], GL40.GL_TESS_EVALUATION_SHADER);
			geomID = loadShader(shaderName[3], GL32.GL_GEOMETRY_SHADER);
			fragID = loadShader(shaderName[4], GL20.GL_FRAGMENT_SHADER);

			GL20.glAttachShader(programID, vertID);
			GL20.glAttachShader(programID, tessContID);
			GL20.glAttachShader(programID, tessEvalID);
			GL20.glAttachShader(programID, geomID);
			GL20.glAttachShader(programID, fragID);

		} else if (shaderName.length == 2) {
			vertID = loadShader(shaderName[0], GL20.GL_VERTEX_SHADER);
			fragID = loadShader(shaderName[1], GL20.GL_FRAGMENT_SHADER);

			GL20.glAttachShader(programID, vertID);
			GL20.glAttachShader(programID, fragID);
		}

		GL20.glLinkProgram(programID);

		setupShaderVariables();

		GL20.glValidateProgram(programID);

		exitOnGLError("setupShaders");
	}

	public abstract void setupShaderVariables();

	public abstract void copyModelSpecificUniformsToShader(MatrixStack modelMatrix);

	public abstract void copySharedUniformsToShader(Vector4 lightPosCameraSpace, MaterialParams materialParams);

	public abstract void copyShaderSpecificUniformsToShader();

	public void destroy() {
		GL20.glDetachShader(programID, vertID);
		GL20.glDetachShader(programID, fragID);
		if (tessellation) {
			GL20.glDetachShader(programID, tessContID);
			GL20.glDetachShader(programID, tessEvalID);
			GL20.glDetachShader(programID, geomID);
		}

		GL20.glDeleteShader(vertID);
		GL20.glDeleteShader(fragID);
		if (tessellation) {
			GL20.glDeleteShader(tessContID);
			GL20.glDeleteShader(tessEvalID);
			GL20.glDeleteShader(geomID);
		}

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

}
