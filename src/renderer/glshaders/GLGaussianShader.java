package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3;
import static org.lwjgl.opengl.GL20.glUniform4f;

import java.nio.FloatBuffer;

import maths.types.Matrix3;
import maths.types.MatrixStack;
import maths.types.Vector4;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Vector4f;

import renderer.GLWorld;
import renderer.MaterialParams;

public class GLGaussianShader extends GLShader {

	private final float lightAttenuation = 3.7f;
	private final float shininessFactor = 2.0f;
	private final float DIR_LIGHT_INTENSITY = 5;
	private final float AMB_LIGHT_INTENSITY = 0;
	private Vector4f lightPos;
	// private final float lightHeight = 2f;

	// int modelToCameraMatrixUnif;
	// int normalModelToCameraMatrixUnif;

	private int modelToCameraMatrixUniform;

	private int lightIntensityUniform;
	private int ambientIntensityUniform;

	private int normalModelToCameraMatrixUniform;
	private int cameraSpaceLightPositionUniform;
	private int lightAttenuationUniform;
	private int shininessFactorUniform;

	FloatBuffer vector4Buffer = null;

	// This is used at the start of the program...
	// private final int projectionBlockIndex = 2;

	public GLGaussianShader(GLWorld glWorld, int projectionBlockIndex) {
		super(glWorld, projectionBlockIndex);
		vector4Buffer = BufferUtils.createFloatBuffer(4);

		lightPos = new Vector4f(0.0f, 0.0f, 1.5f, 1.0f);
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
		glUniform4f(lightIntensityUniform, 0.8f, 0.8f, 0.8f, 1);
		glUniform4f(ambientIntensityUniform, 0.2f, 0.2f, 0.2f, 1);
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
