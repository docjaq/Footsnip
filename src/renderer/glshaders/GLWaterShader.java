package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3;
import static org.lwjgl.opengl.GL20.glUniform4f;
import math.types.Matrix3;
import math.types.MatrixStack;
import math.types.Vector4;

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
	public void copyShaderSpecificUniformsToShader() {

		// All the variables but time are set here. Not sure when to set the
		// time. Look at Shader class to investigate. Also, why does it loop
		// through four, but the arrays are [8] long in the shader?

		// numWaves = 4
		// waterHeight = 4
		/*
		 * for (int i = 0; i < 4; ++i) { float amplitude = 0.5f / (i + 1);
		 * waterShader->setUniform(format("amplitude[%d]", i), amplitude);
		 * 
		 * float wavelength = 8 * M_PI / (i + 1);
		 * waterShader->setUniform(format("wavelength[%d]", i), wavelength);
		 * 
		 * float speed = 1.0f + 2*i; waterShader->setUniform(format("speed[%d]",
		 * i), speed);
		 * 
		 * float angle = uniformRandomInRange(-M_PI/3, M_PI/3);
		 * waterShader->setUniform(format("direction[%d]", i), cos(angle),
		 * sin(angle)); }
		 */
	}
}
