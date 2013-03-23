package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

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

}
