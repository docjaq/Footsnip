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
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import renderer.MaterialParams;
import samplers.CubeMap;
import samplers.Texture2D;

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
	private int averageWaveDirectionUniform;

	// Textures
	private CubeMap cubeMap;
	private int cubeMapUniform;
	private int cubeMapTexUnit = 4;

	private Texture2D normalMap;
	private int normalMapTexUnit = 3;

	// private int normalMapUniform;
	// private int normalMapLocation = -1;
	// private int normalMapTexUnit = 10;

	public GLWaterShader(int projectionBlockIndex, CubeMap cubeMap, Texture2D normalMap) {
		super(projectionBlockIndex);
		this.cubeMap = cubeMap;
		this.normalMap = normalMap;
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
		normalMap.setUniformLocation(GL20.glGetUniformLocation(programID, "normalMap"));
		averageWaveDirectionUniform = GL20.glGetUniformLocation(programID, "averageWaveDirection");

		int projectionBlock = GL31.glGetUniformBlockIndex(programID, "Projection");
		GL31.glUniformBlockBinding(programID, projectionBlock, projectionBlockIndex);

		setupSamplerUBO();
	}

	private void setupSamplerUBO() {

		bindShader();
		GL20.glUniform1i(cubeMapUniform, cubeMapTexUnit);
		// System.out.println("Cube map ID " + cubeMap.getTexId());
		GL20.glUniform1i(normalMap.getUniformLocation(), normalMapTexUnit);
		// System.out.println("Normal map ID " + normalMap.getTexId());
		// System.out.println("Normal map uniform " +
		// normalMap.getUniformLocation());
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
		time += 0.0000008; // 0.000008
		glUniform1f(timeUniform, time);
	}

	@Override
	public void copyShaderSpecificUniformsToShaderInit() {

		// Supports up to four waves
		int numberOfWaves = 4;
		float[] amplitude = new float[numberOfWaves];
		float[] wavelength = new float[numberOfWaves];
		float[] speed = new float[numberOfWaves];
		Vector2[] direction = new Vector2[numberOfWaves];
		Vector2 averageWaveDirection = new Vector2();

		GL20.glUniform1i(numWavesUniform, numberOfWaves);
		glUniform1f(waterHeightUniform, -0.39f);

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

			averageWaveDirection.x(averageWaveDirection.x() + direction[i].x());
			averageWaveDirection.y(averageWaveDirection.y() + direction[i].y());
		}
		averageWaveDirection.x(averageWaveDirection.x() / (float) numberOfWaves);
		averageWaveDirection.y(averageWaveDirection.y() / (float) numberOfWaves);
		GL20.glUniform2f(averageWaveDirectionUniform, averageWaveDirection.x(), averageWaveDirection.y());

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
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + cubeMapTexUnit);
		cubeMap.bind();

		// TODO: For some reason, I cannot enable this... :(
		// GL13.glActiveTexture(GL13.GL_TEXTURE0 + normalMapTexUnit);
		normalMap.bind();

		// GL13.glActiveTexture(GL13.GL_TEXTURE0 + normalMap.getTexId());
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		// normalMap.getUniformLocation());
	}

	public void unbindCubeMap() {
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public Texture2D getNormalMap() {
		return normalMap;
	}
}
