package renderer.glshaders;

import static org.lwjgl.opengl.GL20.glUniform1f;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

public class GLGaussianTessellationShader extends GLGaussianShader {

	protected int tessLevelInner;
	protected int tesLevelOuter;

	public GLGaussianTessellationShader(int projectionBlockIndex) {
		super(projectionBlockIndex);
	}

	@Override
	public void setupShaderVariables() {

		// Vertex shader uniforms
		modelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "modelToCameraMatrix");
		normalModelToCameraMatrixUniform = GL20.glGetUniformLocation(programID, "normalModelToCameraMatrix");

		// Tessellation Evaluation shader uniforms
		tessLevelInner = GL20.glGetUniformLocation(programID, "TessLevelInner");
		tesLevelOuter = GL20.glGetUniformLocation(programID, "TessLevelOuter");

		// Fragment shader uniforms
		lightIntensityUniform = GL20.glGetUniformLocation(programID, "lightIntensity");
		ambientIntensityUniform = GL20.glGetUniformLocation(programID, "ambientIntensity");
		cameraSpaceLightPositionUniform = GL20.glGetUniformLocation(programID, "cameraSpaceLightPos");
		lightAttenuationUniform = GL20.glGetUniformLocation(programID, "lightAttenuation");
		shininessFactorUniform = GL20.glGetUniformLocation(programID, "shininessFactor");

		int projectionBlock = GL31.glGetUniformBlockIndex(programID, "Projection");
		GL31.glUniformBlockBinding(programID, projectionBlock, projectionBlockIndex);
	}

	@Override
	public void copyTesselationUniformsToShader() {
		glUniform1f(tessLevelInner, 1);
		glUniform1f(tesLevelOuter, 1);
	}
}
