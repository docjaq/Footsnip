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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import renderer.GLCubeMap;
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

	// Textures
	private GLCubeMap cubeMap;
	private int cubeMapUniform;

	private int normalMapUniform;
	private int normalMapLocation = -1;
	private int normalMapTexUnit = 10;

	public GLWaterShader(int projectionBlockIndex, GLCubeMap cubeMap) {
		super(projectionBlockIndex);
		this.cubeMap = cubeMap;
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

		// Vert shader specific
		waterHeightUniform = GL20.glGetUniformLocation(programID, "waterHeight");
		timeUniform = GL20.glGetUniformLocation(programID, "time");
		numWavesUniform = GL20.glGetUniformLocation(programID, "numWaves");
		amplitudeUniform = GL20.glGetUniformLocation(programID, "amplitude");
		wavelengthUniform = GL20.glGetUniformLocation(programID, "wavelength");
		speedUniform = GL20.glGetUniformLocation(programID, "speed");
		directionUniform = GL20.glGetUniformLocation(programID, "direction");

		// Frag shader specific
		cubeMapUniform = GL20.glGetUniformLocation(programID, "cuveMap");
		normalMapUniform = GL20.glGetUniformLocation(programID, "normalMap");

		int projectionBlock = GL31.glGetUniformBlockIndex(programID, "Projection");
		GL31.glUniformBlockBinding(programID, projectionBlock, projectionBlockIndex);

		setupSamplerUBO();
	}

	private void setupSamplerUBO() {

		bindShader();
		GL20.glUniform1i(cubeMapUniform, cubeMap.getTexId());
		GL20.glUniform1i(normalMapUniform, normalMapTexUnit);
		unbindShader();
	}

	// Copy all of the shader uniforms that are shared with all objects (i.e.
	// only need to be sent once)
	@Override
	public void copySharedUniformsToShader(Vector4 lightPosCameraSpace, MaterialParams materialParams) {

		// All for fragment shader
		glUniform4f(lightIntensityUniform, 3f, 3f, 3f, 1);
		glUniform4f(ambientIntensityUniform, 0.3f, 0.3f, 0.3f, 1);
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
		time += 0.000008; // 0.000008
		glUniform1f(timeUniform, time);
	}

	@Override
	public void copyShaderSpecificUniformsToShaderInit() {

		// This works really nicely, but strangely, not from all orientations.
		// Maybe try and even further reduce the amplitude, or fix an angle that
		// looks really nice

		// Supports up to four waves
		int numberOfWaves = 4;
		float[] amplitude = new float[numberOfWaves];
		float[] wavelength = new float[numberOfWaves];
		float[] speed = new float[numberOfWaves];
		Vector2[] direction = new Vector2[numberOfWaves];

		GL20.glUniform1i(numWavesUniform, numberOfWaves);
		glUniform1f(waterHeightUniform, -0.45f);

		float originalAngle = (float) (Math.random() * Math.PI * 2);
		for (int i = 0; i < numberOfWaves; i++) {
			// 0.004f / (i + 1)
			amplitude[i] = 0.008f / (i + 1); // 0.004
			// (float) (0.055 * Math.PI / (float) (i + 1))
			wavelength[i] = (float) (0.06 * Math.PI / (float) (i + 1)); // 0.055
			// 1.0f + 2 * i;
			speed[i] = 1.0f + 2 * i;

			// 2f rather than Math.random()
			float angle = (float) (originalAngle + ((Math.random() < 0.5) ? 1 : -1) * (float) (Math.PI / 20f * i));
			direction[i] = new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
			// System.out.println("Angles: " + Math.atan2(direction[i].y(),
			// direction[i].x()));
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

	public void bindTextures() {
		cubeMap.bind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0 + normalMapTexUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMapLocation);
	}

	public void unbindCubeMap() {
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	private float uniformRandomInRange(double d, double e) {
		assert (d < e);
		double n = Math.random();
		double v = d + n * (e - d);
		return (float) v;
	}

	public int getNormalMapLocation() {
		return normalMapLocation;
	}

	public void setNormalMapLocation(int normalMapLocation) {
		this.normalMapLocation = normalMapLocation;
	}
}
