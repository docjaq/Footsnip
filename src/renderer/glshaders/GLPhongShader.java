package renderer.glshaders;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class GLPhongShader extends GLShader {

	private final float lightAttenuation = 1.2f;
	private final float shininessFactor = 4.0f;
	private final float lightHeight = 1.5f;

	// int modelToCameraMatrixUnif;
	// int normalModelToCameraMatrixUnif;

	int lightIntensityUnif;
	int ambientIntensityUnif;

	int normalMatrixLocation;
	int cameraSpaceLightPosUnif;
	int lightAttenuationUnif;
	int shininessFactorUnif;
	int baseDiffuseColorUnif;

	FloatBuffer matrix33Buffer = null;
	FloatBuffer vector4Buffer = null;

	// This is used at the start of the program...
	// private final int projectionBlockIndex = 2;

	public GLPhongShader() {
		super();

		matrix33Buffer = BufferUtils.createFloatBuffer(9);
		vector4Buffer = BufferUtils.createFloatBuffer(4);
	}

	@Override
	public void setupShaderVariables() {

		projectionMatrixLocation = GL20.glGetUniformLocation(programID, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(programID, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(programID, "modelMatrix");

		normalMatrixLocation = GL20.glGetUniformLocation(programID, "normalMatrix");
		lightIntensityUnif = GL20.glGetUniformLocation(programID, "lightIntensity");
		ambientIntensityUnif = GL20.glGetUniformLocation(programID, "ambientIntensity");

		// normalModelToCameraMatrixUnif = glGetUniformLocation(programID,
		// "normalModelToCameraMatrix");
		cameraSpaceLightPosUnif = GL20.glGetUniformLocation(programID, "cameraSpaceLightPos");
		lightAttenuationUnif = GL20.glGetUniformLocation(programID, "lightAttenuation");
		shininessFactorUnif = GL20.glGetUniformLocation(programID, "shininessFactor");
		baseDiffuseColorUnif = GL20.glGetUniformLocation(programID, "baseDiffuseColor");

		// int projectionBlock = glGetUniformBlockIndex(programID,
		// "Projection");
		// glUniformBlockBinding(programID, projectionBlock,
		// projectionBlockIndex);

	}

	@Override
	public void copyUniformsToShader(Matrix4f modelMatrix, float[] color) {

		modelMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();

		GL20.glUniformMatrix4(getModelMatrixLocation(), false, matrix44Buffer);

		Matrix3f normMatrix = new Matrix3f();
		normMatrix.m00 = modelMatrix.m00;
		normMatrix.m01 = modelMatrix.m01;
		normMatrix.m02 = modelMatrix.m02;
		normMatrix.m10 = modelMatrix.m10;
		normMatrix.m11 = modelMatrix.m11;
		normMatrix.m12 = modelMatrix.m12;
		normMatrix.m20 = modelMatrix.m20;
		normMatrix.m21 = modelMatrix.m21;
		normMatrix.m22 = modelMatrix.m22;

		normMatrix.invert();
		normMatrix.transpose();
		normMatrix.store(matrix33Buffer);
		matrix33Buffer.flip();
		GL20.glUniformMatrix3(normalMatrixLocation, false, matrix33Buffer);

		GL20.glUniform4f(getFragColorLocation(), color[0], color[1], color[2], color[3]);

		GL20.glUniform4f(lightIntensityUnif, 0.8f, 0.8f, 0.8f, 1.0f);
		GL20.glUniform4f(ambientIntensityUnif, 0.2f, 0.2f, 0.2f, 1.0f);

		Vector4f lightPos = new Vector4f(0.0f, lightHeight, 0.0f, 1.0f);
		lightPos.store(vector4Buffer);
		vector4Buffer.flip();

		GL20.glUniform3(cameraSpaceLightPosUnif, vector4Buffer);
		GL20.glUniform1f(lightAttenuationUnif, lightAttenuation);
		GL20.glUniform1f(shininessFactorUnif, shininessFactor);

	}
}
