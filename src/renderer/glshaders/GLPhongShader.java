package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

public class GLPhongShader extends GLShader {

	// int modelToCameraMatrixUnif;

	int lightIntensityUnif;
	int ambientIntensityUnif;

	// int normalModelToCameraMatrixUnif;
	int cameraSpaceLightPosUnif;
	int lightAttenuationUnif;
	int shininessFactorUnif;
	int baseDiffuseColorUnif;

	// This is used at the start of the program...
	private final int projectionBlockIndex = 2;

	public GLPhongShader() {
		super();
	}

	@Override
	public void setupShaderVariables() {

		projectionMatrixLocation = GL20.glGetUniformLocation(programID, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(programID, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(programID, "modelMatrix");

		// modelToCameraMatrixUnif = glGetUniformLocation(programID,
		// "modelToCameraMatrix");
		lightIntensityUnif = glGetUniformLocation(programID, "lightIntensity");
		ambientIntensityUnif = glGetUniformLocation(programID, "ambientIntensity");

		// normalModelToCameraMatrixUnif = glGetUniformLocation(programID,
		// "normalModelToCameraMatrix");
		cameraSpaceLightPosUnif = glGetUniformLocation(programID, "cameraSpaceLightPos");
		lightAttenuationUnif = glGetUniformLocation(programID, "lightAttenuation");
		shininessFactorUnif = glGetUniformLocation(programID, "shininessFactor");
		baseDiffuseColorUnif = glGetUniformLocation(programID, "baseDiffuseColor");

		// int projectionBlock = glGetUniformBlockIndex(programID,
		// "Projection");
		// glUniformBlockBinding(programID, projectionBlock,
		// projectionBlockIndex);

	}

	@Override
	public void copyUniformsToShader(FloatBuffer matrix44Buffer, float[] color) {

		GL20.glUniformMatrix4(getModelMatrixLocation(), false, matrix44Buffer);
		GL20.glUniform4f(getFragColorLocation(), color[0], color[1], color[2], color[3]);

		glUniform4f(lightIntensityUnif, 0.8f, 0.8f, 0.8f, 1.0f);
		glUniform4f(ambientIntensityUnif, 0.2f, 0.2f, 0.2f, 1.0f);

		// IMPLEMENT THIS
		// glUniform3(cameraSpaceLightPosUnif,
		// lightPosCameraSpace.fillAndFlipBuffer(vec4Buffer));
		// glUniform1f(lightAttenuationUnif, lightAttenuation);
		// glUniform1f(shininessFactorUnif, shininessFactor);

	}

}
