package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glUniform1f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

public class GLGaussianTessellationShader extends GLGaussianShader {

	// Tessellation Control shader
	protected int tessLevelInner;
	protected int tesLevelOuter;

	// Tessellation Evaluation shader
	protected int heightMapUniform;

	public GLGaussianTessellationShader(int projectionBlockIndex) {
		super(projectionBlockIndex);
	}

	// Not really sure that this is the best way to set the texture
	private int textureLocation = -1;

	// Shader binding subset
	private int gaussTexUnit = 0;

	// Opengl Shader binding
	private int glTextureUnit = GL13.GL_TEXTURE0;

	// Binding of texture as sampler
	private int gaussSampler;

	@Override
	public void setupShaderVariables() {

		// Vertex shader uniforms
		modelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "modelToCameraMatrix");
		normalModelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "normalModelToCameraMatrix");

		// Tessellation Control shader uniforms
		tessLevelInner = GL20.glGetUniformLocation(programID, "tessLevelInner");
		tesLevelOuter = GL20.glGetUniformLocation(programID, "tessLevelOuter");

		// Tessellation Evaluation shader uniforms
		heightMapUniform = GL20.glGetUniformLocation(programID, "heightMap");

		// Fragment shader uniforms
		lightIntensityUniform = GL20.glGetUniformLocation(programID, "lightIntensity");
		ambientIntensityUniform = GL20.glGetUniformLocation(programID, "ambientIntensity");
		cameraSpaceLightPositionUniform = GL20.glGetUniformLocation(programID, "cameraSpaceLightPos");
		lightAttenuationUniform = GL20.glGetUniformLocation(programID, "lightAttenuation");
		shininessFactorUniform = GL20.glGetUniformLocation(programID, "shininessFactor");

		int projectionBlock = GL31.glGetUniformBlockIndex(programID, "Projection");
		GL31.glUniformBlockBinding(programID, projectionBlock, projectionBlockIndex);

		setupSamplerUBO();
	}

	@Override
	public void copyTesselationUniformsToShader() {
		glUniform1f(tessLevelInner, 4);
		glUniform1f(tesLevelOuter, 4);
	}

	private void setupSamplerUBO() {
		// This is an unusual one. Seems it only needs to be bound once, but
		// needs the actual shader bound when doing it. How odd.
		bindShader();
		GL20.glUniform1i(heightMapUniform, gaussTexUnit);
		unbindShader();
	}

	// This actually stops the interpolation of values, and means that you get
	// step functions rather than smooth values when sampling the heightmap
	public void bindSamplerUnit() {
		// Not sure whether this needs to happen after the OpenGL texture unit
		// has been created
		gaussSampler = GL33.glGenSamplers();
		GL33.glSamplerParameteri(gaussSampler, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL33.glSamplerParameteri(gaussSampler, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		// This clamps the range of the sampler access. If enabled, it will be
		// strictly more accurate, though will result in gaps
		// GL33.glSamplerParameteri(gaussSampler, GL11.GL_TEXTURE_WRAP_S,
		// GL12.GL_CLAMP_TO_EDGE);
		// GL33.glSamplerParameteri(gaussSampler, GL11.GL_TEXTURE_WRAP_T,
		// GL12.GL_CLAMP_TO_EDGE);
	}

	public void bindTexture() {
		GL13.glActiveTexture(glTextureUnit + gaussTexUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureLocation);
		GL33.glBindSampler(gaussTexUnit, gaussSampler);
	}

	public void unbindTexture() {
		GL33.glBindSampler(gaussTexUnit, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public int getGaussTexUnit() {
		return gaussTexUnit;
	}

	public void setTextureLocation(int textureLocation) {
		this.textureLocation = textureLocation;
	}

	public int getTextureLocation() {
		return textureLocation;
	}

}
