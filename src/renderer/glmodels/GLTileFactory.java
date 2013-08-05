package renderer.glmodels;

import org.lwjgl.util.vector.Vector3f;

import renderer.glshaders.GLShader;

public interface GLTileFactory {
	public GLModel create(Vector3f position, Vector3f rotation, float scale, GLShader shader, float[] color, float size);
}
