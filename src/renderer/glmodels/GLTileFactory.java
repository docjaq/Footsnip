package renderer.glmodels;

import maths.types.Vector3;
import assets.world.AbstractTile;

public interface GLTileFactory {
	public GLModel create(AbstractTile tile, Vector3 position, Vector3 rotation, float scale, float size);
}
