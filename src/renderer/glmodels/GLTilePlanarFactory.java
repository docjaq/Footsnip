package renderer.glmodels;

import maths.types.Vector3;
import assets.world.AbstractTile;

public class GLTilePlanarFactory implements GLTileFactory {

	@Override
	public GLTilePlane create(AbstractTile tile, Vector3 position, Vector3 rotation, float scale, float size) {
		return new GLTilePlane(position, rotation, scale, size);
	}

}
