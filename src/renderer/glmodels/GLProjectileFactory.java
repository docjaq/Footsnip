package renderer.glmodels;

import org.lwjgl.util.vector.Vector3f;

import renderer.glshaders.GLShader;

public interface GLProjectileFactory {
	public GLModel create(Vector3f position, Vector3f angle, float scale, GLShader shader, float[] color);

}
