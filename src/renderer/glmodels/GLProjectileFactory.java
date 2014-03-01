package renderer.glmodels;

import maths.types.Vector3;

public interface GLProjectileFactory {
	public GLModel create(Vector3 position, Vector3 angle, float scale, float[] color);

}
