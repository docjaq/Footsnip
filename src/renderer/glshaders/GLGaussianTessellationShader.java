package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glUniform1f;
import math.types.Matrix3;
import math.types.MatrixStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

public class GLGaussianTessellationShader extends GLGaussianShader {

	public GLGaussianTessellationShader(int projectionBlockIndex) {
		super(projectionBlockIndex);
		tessLevelInner = 2;
		tessLevelOuter = 2;
	}

	private int heightMapUniform;
	// Not really sure that this is the best way to set the texture
	private int heightmapLocation = -1;
	// Shader binding subset
	private int heightmapTexUnit = 0;

	protected int colorMapUniform;
	// Not really sure that this is the best way to set the texture
	private int colorMapLocation = -1;
	// Shader binding subset
	private int colorMapTexUnit = 1;

	private int normalMapAUniform;
	// Not really sure that this is the best way to set the texture
	private int normalMapALocation = -1;
	// Shader binding subset
	private int normalMapATexUnit = 2;

	// Binding of texture as sampler
	private int heightmapTextureSampler;

	// Binding of texture as sampler
	private int colormapTextureSampler;

	// Tessellation Control shader
	private int tessLevelInnerUniform;
	private int tessLevelOuterUniform;

	private int tessLevelInner;
	private int tessLevelOuter;

	@Override
	public void setupShaderVariables() {

		// Vertex shader uniforms
		modelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "modelToCameraMatrix");
		normalModelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "normalModelToCameraMatrix");

		// Tessellation Control shader uniforms
		tessLevelInnerUniform = GL20.glGetUniformLocation(programID, "tessLevelInner");
		tessLevelOuterUniform = GL20.glGetUniformLocation(programID, "tessLevelOuter");

		// Tessellation Evaluation shader uniforms
		heightMapUniform = GL20.glGetUniformLocation(programID, "heightMap");
		colorMapUniform = GL20.glGetUniformLocation(programID, "testColorMap");
		normalMapAUniform = GL20.glGetUniformLocation(programID, "normalMapA");

		// Fragment shader uniforms
		lightIntensityUniform = GL20.glGetUniformLocation(programID, "lightIntensity");
		ambientIntensityUniform = GL20.glGetUniformLocation(programID, "ambientIntensity");
		cameraSpaceLightPositionUniform = GL20.glGetUniformLocation(programID, "cameraSpaceLightPos");
		lightAttenuationUniform = GL20.glGetUniformLocation(programID, "lightAttenuation");
		shininessFactorUniform = GL20.glGetUniformLocation(programID, "shininessFactor");

		int projectionBlock = GL31.glGetUniformBlockIndex(programID, "Projection");
		GL31.glUniformBlockBinding(programID, projectionBlock, projectionBlockIndex);

		setupSamplerUBO();

		// Create sampler object for use when binding textures at runtime
		setupHeightmapSampler();
		setupColormapSampler();
	}

	@Override
	public void copyShaderSpecificUniformsToShaderInit() {
		GL20.glUniform1i(normalMapAUniform, normalMapATexUnit);
		System.out.println("LATE Normal map texID " + normalMapALocation + " and texUnit" + normalMapATexUnit);
	}

	@Override
	public void copyModelSpecificUniformsToShader(MatrixStack modelMatrix) {
		// All for vertex shader
		GL20.glUniformMatrix4(modelToCameraMatrixUniform, false, modelMatrix.getTop().toBuffer());
		GL20.glUniformMatrix3(normalModelToCameraMatrixUniform, false, new Matrix3(modelMatrix.getTop()).inverse().transpose().toBuffer());
		glUniform1f(tessLevelInnerUniform, tessLevelInner);
		glUniform1f(tessLevelOuterUniform, tessLevelOuter);
	}

	// Uniform buffer objects
	private void setupSamplerUBO() {
		bindShader();
		GL20.glUniform1i(heightMapUniform, heightmapTexUnit);
		// System.out.println("Early height map texID " + heightmapLocation +
		// " and texUnit " + heightmapTexUnit);
		GL20.glUniform1i(colorMapUniform, colorMapTexUnit);
		// System.out.println("Early colorMap map texID " + colorMapLocation +
		// " and texUnit " + colorMapTexUnit);
		GL20.glUniform1i(normalMapAUniform, normalMapATexUnit);
		// System.out.println("Early Normal map texID " + normalMapALocation +
		// " and texUnit " + normalMapATexUnit);
		unbindShader();
	}

	// This actually stops the interpolation of values, and means that you get
	// step functions rather than smooth values when sampling the heightmap
	public void setupHeightmapSampler() {
		// Not sure whether this needs to happen after the OpenGL texture unit
		// has been created
		heightmapTextureSampler = GL33.glGenSamplers();
		GL33.glSamplerParameteri(heightmapTextureSampler, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL33.glSamplerParameteri(heightmapTextureSampler, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		// This clamps the range of the sampler access. If enabled, it will be
		// strictly more accurate, though will result in gaps
		GL33.glSamplerParameteri(heightmapTextureSampler, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL33.glSamplerParameteri(heightmapTextureSampler, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	}

	public void setupColormapSampler() {
		// Not sure whether this needs to happen after the OpenGL texture unit
		// has been created
		colormapTextureSampler = GL33.glGenSamplers();
		GL33.glSamplerParameteri(colormapTextureSampler, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL33.glSamplerParameteri(colormapTextureSampler, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		// This clamps the range of the sampler access. If enabled, it will be
		// strictly more accurate, though will result in gaps
		GL33.glSamplerParameteri(colormapTextureSampler, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL33.glSamplerParameteri(colormapTextureSampler, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	}

	public void bindTextures() {
		GL33.glBindSampler(heightmapTexUnit, heightmapTextureSampler);
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + heightmapTexUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, heightmapLocation);
		// GL20.glUniform1i(heightMapUniform, heightmapTexUnit);// May be wrong

		// GL33.glBindSampler(colorMapTexUnit, colormapTextureSampler);
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + colorMapTexUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_1D, colorMapLocation);
		// GL20.glUniform1i(colorMapUniform, colorMapTexUnit);

		GL13.glActiveTexture(GL13.GL_TEXTURE0 + normalMapATexUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMapALocation);
	}

	public void unbindTextures() {
		GL33.glBindSampler(heightmapTexUnit, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL33.glBindSampler(colorMapTexUnit, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_1D, 0);

		GL33.glBindSampler(normalMapATexUnit, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	// public void bindNormalmap() {
	// GL13.glActiveTexture(GL13.GL_TEXTURE0 + normalmapTexUnit);
	// GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalmapLocation);
	// GL20.glUniform1i(normalmapUniform, normalmapTexUnit); // May be wrong
	// // GL33.glBindSampler(normalmapTexUnit, sampler);
	//
	// }
	//
	// public void unbindNormalmap() {
	// GL33.glBindSampler(normalmapTexUnit, 0);
	// GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	// }

	public void setHeightmapLocation(int heightmapLocation) {
		this.heightmapLocation = heightmapLocation;
	}

	public int getHeightmapLocation() {
		return heightmapLocation;
	}

	public void setColorMapLocation(int colorMapLocation) {
		this.colorMapLocation = colorMapLocation;
	}

	public int getColorMapLocation() {
		return colorMapLocation;
	}

	public int getTessLevelInner() {
		return tessLevelInner;
	}

	public void setTessLevelInner(int tessLevelInner) {
		this.tessLevelInner = tessLevelInner;
	}

	public int getTessLevelOuter() {
		return tessLevelOuter;
	}

	public void setTessLevelOuter(int tessLevelOuter) {
		this.tessLevelOuter = tessLevelOuter;
	}

	public int getNormalMapALocation() {
		return normalMapALocation;
	}

	public void setNormalMapALocation(int normalMapALocation) {
		this.normalMapALocation = normalMapALocation;
	}

	// public int getNormalmapTexUnit() {
	// return normalmapTexUnit;
	// }
	//
	// public void setNormalmapLocation(int normalmapLocation) {
	// this.normalmapLocation = normalmapLocation;
	// }
	//
	// public int getNormalmapLocation() {
	// return normalmapLocation;
	// }

}
