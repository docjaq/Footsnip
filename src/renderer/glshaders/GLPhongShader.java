package renderer.glshaders;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class GLPhongShader extends GLShader {

	private final float lightAttenuation = 0.7f;
	private final float shininessFactor = 2.0f;
	// private final float lightHeight = 2f;

	// int modelToCameraMatrixUnif;
	// int normalModelToCameraMatrixUnif;

	int lightIntensityUnif;
	int ambientIntensityUnif;

	int normalMatrixLocation;
	int cameraSpaceLightPosUnif;
	int lightAttenuationUnif;
	int shininessFactorUnif;
	int baseDiffuseColorUnif;

	FloatBuffer vector4Buffer = null;

	// This is used at the start of the program...
	// private final int projectionBlockIndex = 2;

	public GLPhongShader() {
		super();
		vector4Buffer = BufferUtils.createFloatBuffer(4);
	}

	@Override
	public void setupShaderVariables() {

		projectionMatrixLocation = GL20.glGetUniformLocation(programID, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(programID, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(programID, "modelMatrix");

		// TODO: I've just taken this from the GLTexturedShader; without it I
		// get an 'invalid operation' error, on the line
		// GL20.glUniform4f(getFragColorLocation(), color[0], color[1],
		// color[2], color[3]); in copyUniformsToShader(). I don't know if it's
		// right, but it makes it work for me :)
		// Allows for a colour in the fragment shader
		fragColorLocation = GL20.glGetUniformLocation(programID, "fragColor");

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

		Matrix4f normMatrix = new Matrix4f();
		normMatrix.load(modelMatrix);
		/*
		 * normMatrix.m00 = modelMatrix.m00; normMatrix.m01 = modelMatrix.m01;
		 * normMatrix.m02 = modelMatrix.m02; normMatrix.m03 = modelMatrix.m03;
		 * normMatrix.m10 = modelMatrix.m10; normMatrix.m11 = modelMatrix.m11;
		 * normMatrix.m12 = modelMatrix.m12; normMatrix.m13 = modelMatrix.m13;
		 * normMatrix.m20 = modelMatrix.m20; normMatrix.m21 = modelMatrix.m21;
		 * normMatrix.m22 = modelMatrix.m22; normMatrix.m23 = modelMatrix.m23;
		 */

		normMatrix.invert();
		normMatrix.transpose();
		normMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(normalMatrixLocation, false, matrix44Buffer);

		GL20.glUniform4f(getFragColorLocation(), color[0], color[1], color[2], color[3]);

		GL20.glUniform4f(lightIntensityUnif, 1.8f, 1.8f, 1.8f, 1.0f);
		GL20.glUniform4f(ambientIntensityUnif, 0.4f, 0.4f, 0.4f, 1.0f);

		Vector4f lightPos = new Vector4f(0.0f, 0.0f, 2.5f, 1.0f);
		lightPos.store(vector4Buffer);
		vector4Buffer.flip();

		GL20.glUniform3(cameraSpaceLightPosUnif, vector4Buffer);
		GL20.glUniform1f(lightAttenuationUnif, lightAttenuation);
		GL20.glUniform1f(shininessFactorUnif, shininessFactor);

	}
}
