package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3;
import static org.lwjgl.opengl.GL20.glUniform4f;

import java.nio.FloatBuffer;

import math.types.Matrix3;
import math.types.MatrixStack;
import math.types.Vector2;
import math.types.Vector4;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import renderer.MaterialParams;

public class GLWaterShader extends GLShader {

	protected final float lightAttenuation = 3.7f;

	// Generic shared
	protected int modelToCameraMatrixUniform;
	protected int lightIntensityUniform;
	protected int ambientIntensityUniform;
	protected int lightAttenuationUniform;
	protected int shininessFactorUniform;

	// Generic model
	protected int normalModelToCameraMatrixUniform;
	protected int cameraSpaceLightPositionUniform;

	// Shader specific
	protected int waterHeightUniform;
	protected int timeUniform;
	protected int numWavesUniform;
	protected int amplitudeUniform;
	protected int wavelengthUniform;
	protected int speedUniform;
	protected int directionUniform;

	public GLWaterShader(int projectionBlockIndex) {
		super(projectionBlockIndex);
	}

	float time = 0;

	// Bind the variables here
	@Override
	public void setupShaderVariables() {

		// Vertex shader uniforms
		modelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "modelToCameraMatrix");
		normalModelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "normalModelToCameraMatrix");

		// Fragment shader uniforms
		lightIntensityUniform = GL20.glGetUniformLocation(programID, "lightIntensity");
		ambientIntensityUniform = GL20.glGetUniformLocation(programID, "ambientIntensity");
		cameraSpaceLightPositionUniform = GL20.glGetUniformLocation(programID, "cameraSpaceLightPos");
		lightAttenuationUniform = GL20.glGetUniformLocation(programID, "lightAttenuation");
		shininessFactorUniform = GL20.glGetUniformLocation(programID, "shininessFactor");

		// Shader specific
		waterHeightUniform = GL20.glGetUniformLocation(programID, "waterHeight");
		timeUniform = GL20.glGetUniformLocation(programID, "time");
		numWavesUniform = GL20.glGetUniformLocation(programID, "numWaves");
		amplitudeUniform = GL20.glGetUniformLocation(programID, "amplitude");
		wavelengthUniform = GL20.glGetUniformLocation(programID, "wavelength");
		speedUniform = GL20.glGetUniformLocation(programID, "speed");
		directionUniform = GL20.glGetUniformLocation(programID, "direction");

		int projectionBlock = GL31.glGetUniformBlockIndex(programID, "Projection");
		GL31.glUniformBlockBinding(programID, projectionBlock, projectionBlockIndex);

	}

	// TODO: Add a 'Copy once to shader' type method for things that never
	// change

	// Copy all of the shader uniforms that are shared with all objects (i.e.
	// only need to be sent once)
	@Override
	public void copySharedUniformsToShader(Vector4 lightPosCameraSpace, MaterialParams materialParams) {

		// All for fragment shader
		glUniform4f(lightIntensityUniform, 3.8f, 3.8f, 3.8f, 1);
		glUniform4f(ambientIntensityUniform, 0.05f, 0.05f, 0.05f, 1);
		glUniform3(cameraSpaceLightPositionUniform, lightPosCameraSpace.toBuffer());
		glUniform1f(lightAttenuationUniform, lightAttenuation);
		glUniform1f(shininessFactorUniform, materialParams.getSpecularValue());

	}

	// Copy the shaders that are specific to this model (i.e. any translations
	// required. Possibly other things?
	@Override
	public void copyModelSpecificUniformsToShader(MatrixStack modelMatrix) {
		// All for vertex shader
		GL20.glUniformMatrix4(modelToCameraMatrixUniform, false, modelMatrix.getTop().toBuffer());
		GL20.glUniformMatrix3(normalModelToCameraMatrixUniform, false, new Matrix3(modelMatrix.getTop()).inverse().transpose().toBuffer());
	}

	@Override
	public void copyShaderSpecificUniformsToShaderRuntime() {
		time += 0.00001;
		glUniform1f(timeUniform, time);
	}

	@Override
	public void copyShaderSpecificUniformsToShaderInit() {

		// This works really nicely, but strangely, not from all orientations.
		// Maybe try and even further reduce the aplitude, or fix an angle that
		// looks really nice

		// Supports up to four waves
		int numberOfWaves = 4;
		float[] amplitude = new float[numberOfWaves];
		float[] wavelength = new float[numberOfWaves];
		float[] speed = new float[numberOfWaves];
		Vector2[] direction = new Vector2[numberOfWaves];

		GL20.glUniform1i(numWavesUniform, numberOfWaves);
		glUniform1f(waterHeightUniform, -0.45f);

		for (int i = 0; i < numberOfWaves; i++) {
			amplitude[i] = 0.009f / (i + 1); // 0.01
			wavelength[i] = (float) (0.06 * Math.PI / (float) (i + 1)); // 0.08
			speed[i] = 1.0f + 2 * i;

			// 2f rather than Math.random()
			float angle = (float) Math.random() + 1 + (float) (-(Math.PI / 10) + (Math.PI / 10) * i);
			direction[i] = new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
		}

		FloatBuffer buffer = BufferUtils.createFloatBuffer(amplitude.length);
		buffer.put(amplitude);
		buffer.rewind();
		GL20.glUniform1(amplitudeUniform, buffer);

		buffer.clear();
		buffer.put(wavelength);
		buffer.rewind();
		GL20.glUniform1(wavelengthUniform, buffer);

		buffer.clear();
		buffer.put(speed);
		buffer.rewind();
		GL20.glUniform1(speedUniform, buffer);

		for (int i = 0; i < numberOfWaves; i++) {
			int location = GL20.glGetUniformLocation(programID, "direction[" + i + "]");
			GL20.glUniform2f(location, direction[i].x(), direction[i].y());
		}

	}

	private float uniformRandomInRange(float min, float max) {
		assert (min < max);
		double n = Math.random();
		double v = min + n * (max - min);
		return (float) v;
	}
}
