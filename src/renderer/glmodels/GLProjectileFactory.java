package renderer.glmodels;

import org.lwjgl.util.vector.Vector3f;

public interface GLProjectileFactory {
	public GLModel create(Vector3f position, Vector3f angle, float scale, float[] color);

}
