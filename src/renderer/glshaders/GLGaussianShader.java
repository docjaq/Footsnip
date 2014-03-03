package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3;
import static org.lwjgl.opengl.GL20.glUniform4f;
import maths.types.Matrix3;
import maths.types.MatrixStack;
import maths.types.Vector4;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import renderer.MaterialParams;

public class GLGaussianShader extends GLShader {

	private final float lightAttenuation = 3.7f;

	private int modelToCameraMatrixUniform;

	private int lightIntensityUniform;
	private int ambientIntensityUniform;

	private int normalModelToCameraMatrixUniform;
	private int cameraSpaceLightPositionUniform;
	private int lightAttenuationUniform;
	private int shininessFactorUniform;

	public GLGaussianShader(int projectionBlockIndex) {
		super(projectionBlockIndex);
	}

	@Override
	public void setupShaderVariables() {

		modelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "modelToCameraMatrix");
		lightIntensityUniform = GL20.glGetUniformLocation(programID, "lightIntensity");
		ambientIntensityUniform = GL20.glGetUniformLocation(programID, "ambientIntensity");

		normalModelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "normalModelToCameraMatrix");
		cameraSpaceLightPositionUniform = GL20.glGetUniformLocation(programID, "cameraSpaceLightPos");
		lightAttenuationUniform = GL20.glGetUniformLocation(programID, "lightAttenuation");
		shininessFactorUniform = GL20.glGetUniformLocation(programID, "shininessFactor");

		int projectionBlock = GL31.glGetUniformBlockIndex(programID, "Projection");
		GL31.glUniformBlockBinding(programID, projectionBlock, projectionBlockIndex);

	}

	// Copy all of the shader uniforms that are shared with all objects (i.e.
	// only need to be sent once)
	@Override
	public void copySharedUniformsToShader(Vector4 lightPosCameraSpace, MaterialParams materialParams) {
		glUniform4f(lightIntensityUniform, 1.8f, 1.8f, 1.8f, 1);
		glUniform4f(ambientIntensityUniform, 0.05f, 0.05f, 0.05f, 1);
		glUniform3(cameraSpaceLightPositionUniform, lightPosCameraSpace.toBuffer());
		glUniform1f(lightAttenuationUniform, lightAttenuation);
		glUniform1f(shininessFactorUniform, materialParams.getSpecularValue());

	}

	// Copy the shaders that are specific to this model (i.e. any translations
	// required. Possibly other things?
	@Override
	public void copySpecificUniformsToShader(MatrixStack modelMatrix) {
		GL20.glUniformMatrix4(modelToCameraMatrixUniform, false, modelMatrix.getTop().toBuffer());
		GL20.glUniformMatrix3(normalModelToCameraMatrixUniform, false, new Matrix3(modelMatrix.getTop()).inverse().transpose().toBuffer());
	}
}
