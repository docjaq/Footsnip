package renderer.glmodels;

import org.lwjgl.util.vector.Vector3f;

import assets.world.AbstractTile;

public interface GLTileFactory {
	public GLModel create(AbstractTile tile, Vector3f position, Vector3f rotation, float scale, float[] color, float size);
}
